// Throwaway test script -- validates narration TTS (ElevenLabs) in isolation,
// using a fixed fake scene instead of needing real Claude output.
//
// Usage:
//   cd worker
//   export ELEVENLABS_API_KEY="your-real-key"
//   node scripts/test-tts.js

import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { generateNarration } from '../src/pipeline/tts.js';

const outDir = path.join(process.cwd(), 'tmp-test-output');
fs.mkdirSync(outDir, { recursive: true });

const fakeScenes = [
  { narration: 'A cat gets into a perfect yoga pose, stretching in the morning sun.' }
];

console.log('Generating 1 test narration clip via ElevenLabs...');

try {
  const paths = await generateNarration(fakeScenes, 'test-job', outDir);
  console.log(`\nSaved audio to: ${paths[0]}`);
  console.log('Play that file to confirm the narration sounds right.');
} catch (err) {
  console.error('TTS generation failed:', err.message);
  if (err.response?.data) {
    // axios error bodies (the actual reason from ElevenLabs) live here --
    // err.message alone is just the generic HTTP status line.
    const body = Buffer.isBuffer(err.response.data)
      ? err.response.data.toString('utf-8')
      : JSON.stringify(err.response.data);
    console.error('Response body:', body);
  }
  process.exit(1);
}
