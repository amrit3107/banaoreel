import ffmpeg from 'fluent-ffmpeg';
import path from 'path';
import fs from 'fs';

const WIDTH = 1080;
const HEIGHT = 1920; // vertical, matches short-form (Reels/Shorts) aspect ratio
const FPS = 25;

function getDuration(filePath) {
  return new Promise((resolve, reject) => {
    ffmpeg.ffprobe(filePath, (err, data) => {
      if (err) return reject(err);
      resolve(data.format.duration);
    });
  });
}

// ffmpeg drawtext needs these characters escaped inside the filter string.
function escapeDrawtext(text) {
  return text
    .replace(/\\/g, '\\\\')
    .replace(/:/g, '\\:')
    .replace(/'/g, "\\'")
    .replace(/%/g, '\\%');
}

/**
 * One scene = the still image with a slow Ken Burns zoom, its narration audio,
 * and a burned-in caption of the narration text at the bottom.
 */
function buildSceneClip({ imagePath, audioPath, narration, outPath }) {
  return new Promise((resolve, reject) => {
    getDuration(audioPath).then(duration => {
      const zoomFrames = Math.round(duration * FPS);
      const caption = escapeDrawtext(narration);

      ffmpeg()
        .input(imagePath)
        .loop(duration)
        .input(audioPath)
        .complexFilter([
          // Slight oversize + slow zoom for the Ken Burns effect, cropped to target size
          `[0:v]scale=${Math.round(WIDTH * 1.15)}:${Math.round(HEIGHT * 1.15)},` +
          `zoompan=z='min(zoom+0.0007,1.15)':d=${zoomFrames}:s=${WIDTH}x${HEIGHT}:fps=${FPS}[zoomed]`,
          // Caption box near the bottom, wrapped by ffmpeg's own line breaking via box padding
          `[zoomed]drawtext=text='${caption}':fontcolor=white:fontsize=42:` +
          `box=1:boxcolor=black@0.55:boxborderw=18:x=(w-text_w)/2:y=h-th-140:line_spacing=10[captioned]`
        ])
        .outputOptions([
          '-map', '[captioned]',
          '-map', '1:a',
          '-shortest',
          '-c:v', 'libx264',
          '-c:a', 'aac',
          '-pix_fmt', 'yuv420p'
        ])
        .duration(duration)
        .on('error', reject)
        .on('end', () => resolve(outPath))
        .save(outPath);
    }).catch(reject);
  });
}

function concatClips(clipPaths, outDir, jobId) {
  return new Promise((resolve, reject) => {
    const concatListPath = path.join(outDir, 'concat.txt');
    fs.writeFileSync(concatListPath, clipPaths.map(p => `file '${p}'`).join('\n'));

    const outputPath = path.join(outDir, `${jobId}_novoice_mix.mp4`);

    ffmpeg()
      .input(concatListPath)
      .inputOptions(['-f', 'concat', '-safe', '0'])
      .outputOptions(['-c', 'copy'])
      .on('error', reject)
      .on('end', () => resolve(outputPath))
      .save(outputPath);
  });
}

/**
 * Mixes a background music bed under the narration at low volume, if one is
 * configured. MUSIC_BED_PATH is optional — without it, the concatenated file
 * (narration only) is used as the final output.
 */
function mixMusicBed(videoPath, outDir, jobId) {
  const musicPath = process.env.MUSIC_BED_PATH;
  if (!musicPath || !fs.existsSync(musicPath)) {
    return Promise.resolve(videoPath);
  }

  return new Promise((resolve, reject) => {
    const outputPath = path.join(outDir, `${jobId}.mp4`);

    ffmpeg()
      .input(videoPath)
      .input(musicPath)
      .complexFilter([
        // Loop the music bed under the narration at ~15% volume, duck under voice
        `[1:a]volume=0.15,aloop=loop=-1:size=2e9[music]`,
        `[0:a][music]amix=inputs=2:duration=first:dropout_transition=2[mixed]`
      ])
      .outputOptions(['-map', '0:v', '-map', '[mixed]', '-c:v', 'copy', '-c:a', 'aac', '-shortest'])
      .on('error', reject)
      .on('end', () => resolve(outputPath))
      .save(outputPath);
  });
}

export async function assembleVideo({ imagePaths, audioPaths, scenes, jobId, outDir }) {
  const clipPaths = [];
  for (let i = 0; i < scenes.length; i++) {
    const clipPath = path.join(outDir, `clip_${i}.mp4`);
    await buildSceneClip({
      imagePath: imagePaths[i],
      audioPath: audioPaths[i],
      narration: scenes[i].narration,
      outPath: clipPath
    });
    clipPaths.push(clipPath);
  }

  const concatenated = await concatClips(clipPaths, outDir, jobId);
  const finalPath = await mixMusicBed(concatenated, outDir, jobId);

  // If mixMusicBed skipped (no bed configured), rename the concatenated file
  // to the expected final filename so storage.js always finds `${jobId}.mp4`.
  const expectedPath = path.join(outDir, `${jobId}.mp4`);
  if (finalPath !== expectedPath) {
    fs.renameSync(finalPath, expectedPath);
    return expectedPath;
  }
  return finalPath;
}
