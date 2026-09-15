import axios from 'axios';
import fs from 'fs';

/**
 * Synthesizes narration audio per scene via ElevenLabs.
 * TODO: wire real ElevenLabs API call and voice ID selection.
 */
export async function generateNarration(scenes, jobId) {
  const audioPaths = [];
  for (let i = 0; i < scenes.length; i++) {
    const outPath = `/tmp/banaoreel-render/${jobId}/scene_${i}.mp3`;
    // TODO: call ElevenLabs TTS API, write returned audio bytes to outPath
    audioPaths.push(outPath);
  }
  return audioPaths;
}
