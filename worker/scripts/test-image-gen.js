// Throwaway test script -- validates image generation (OpenAI) in isolation,
// using a fixed fake scene instead of needing real Claude output.
//
// Usage:
//   cd worker
//   export IMAGE_GEN_API_KEY="your-real-key"
//   node scripts/test-image-gen.js

import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { generateImages } from '../src/pipeline/imageGen.js';

const outDir = path.join(process.cwd(), 'tmp-test-output');
fs.mkdirSync(outDir, { recursive: true });

const fakeScenes = [
  { narration: 'A cat gets into a yoga pose.', imagePrompt: 'a cute cat doing a yoga downward dog pose on a mat, studio lighting' }
];

console.log('Generating 1 test image via OpenAI...');

try {
  const paths = await generateImages(fakeScenes, 'test-job', outDir);
  console.log(`\nSaved image to: ${paths[0]}`);
  console.log('Open that file to confirm it looks right.');
} catch (err) {
  console.error('Image generation failed:', err.message);
  process.exit(1);
}
