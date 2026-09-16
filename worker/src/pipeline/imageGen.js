import OpenAI from 'openai';
import fs from 'fs';
import path from 'path';

let openai;
function client() {
  if (!openai) openai = new OpenAI({ apiKey: process.env.IMAGE_GEN_API_KEY });
  return openai;
}

/**
 * Generates one image per scene from its imagePrompt using OpenAI's image API.
 * Swap the `openai.images.generate` call for another provider (Flux via
 * Replicate, Imagen, SDXL self-hosted) if that turns out cheaper at scale --
 * the cost comparison from the plan flagged this as the main margin lever.
 */
export async function generateImages(scenes, jobId, outDir) {
  const imagePaths = [];

  for (let i = 0; i < scenes.length; i++) {
    const result = await client().images.generate({
      model: 'gpt-image-1',
      prompt: scenes[i].imagePrompt,
      size: '1024x1536', // portrait, matches short-form vertical video
      n: 1
      // No response_format here on purpose: gpt-image-1 always returns
      // base64 (b64_json) directly and rejects this param outright.
    });

    const outPath = path.join(outDir, `scene_${i}.png`);
    const imageBytes = Buffer.from(result.data[0].b64_json, 'base64');
    fs.writeFileSync(outPath, imageBytes);

    imagePaths.push(outPath);
  }

  return imagePaths;
}
