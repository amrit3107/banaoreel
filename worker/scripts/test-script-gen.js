// Throwaway test script -- validates just the Claude script-generation step
// in isolation, without needing SQS, image gen, TTS, or FFmpeg.
//
// Usage:
//   cd worker
//   export ANTHROPIC_API_KEY="your-real-key"
//   node scripts/test-script-gen.js "a cat doing yoga"

import 'dotenv/config';
import { generateScript } from '../src/pipeline/scriptGen.js';

const prompt = process.argv[2] || 'a cat doing yoga';
const durationSec = 15;

console.log(`Generating script for: "${prompt}" (${durationSec}s)...`);

try {
  const scenes = await generateScript(prompt, durationSec);
  console.log(`\nGot ${scenes.length} scenes:\n`);
  scenes.forEach((scene, i) => {
    console.log(`Scene ${i + 1}:`);
    console.log(`  Narration: ${scene.narration}`);
    console.log(`  Image prompt: ${scene.imagePrompt}\n`);
  });
} catch (err) {
  console.error('Script generation failed:', err.message);
  process.exit(1);
}
