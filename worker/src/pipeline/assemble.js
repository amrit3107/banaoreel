import ffmpeg from 'fluent-ffmpeg';
import path from 'path';

/**
 * Combines per-scene images + narration audio into the final video:
 * Ken Burns pan/zoom on each image, synced to its narration length,
 * burned-in captions, background music bed, single MP4 output.
 * TODO: implement the actual ffmpeg filter graph. This stub documents
 * the intended steps so the real implementation can be filled in incrementally.
 */
export async function assembleVideo({ imagePaths, audioPaths, scenes, jobId, outDir }) {
  const outputPath = path.join(outDir, `${jobId}.mp4`);

  // Step 1: for each scene, build a short clip: image + Ken Burns zoompan + its audio track
  // Step 2: burn in captions (from scenes[i].narration) via drawtext or a subtitles filter
  // Step 3: concat all scene clips into one video
  // Step 4: mix in a background music bed at low volume under the narration
  // Step 5: write final MP4 to outputPath

  return outputPath;
}
