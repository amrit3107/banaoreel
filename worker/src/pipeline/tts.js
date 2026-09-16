import axios from 'axios';
import fs from 'fs';
import path from 'path';

const DEFAULT_VOICE_ID = 'sTuFDs5r9KT8f6JSiJbq'; // from your ElevenLabs "My Voices" — usable on free tier, unlike premade library voices

/**
 * Synthesizes narration audio per scene via ElevenLabs.
 */
export async function generateNarration(scenes, jobId, outDir) {
  const audioPaths = [];

  for (let i = 0; i < scenes.length; i++) {
    const response = await axios.post(
      `https://api.elevenlabs.io/v1/text-to-speech/${DEFAULT_VOICE_ID}`,
      {
        text: scenes[i].narration,
        model_id: 'eleven_multilingual_v2',
        voice_settings: { stability: 0.5, similarity_boost: 0.75 }
      },
      {
        headers: {
          'xi-api-key': process.env.ELEVENLABS_API_KEY,
          'Content-Type': 'application/json'
        },
        responseType: 'arraybuffer'
      }
    );

    const outPath = path.join(outDir, `scene_${i}.mp3`);
    fs.writeFileSync(outPath, response.data);
    audioPaths.push(outPath);
  }

  return audioPaths;
}
