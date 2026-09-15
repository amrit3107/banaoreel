import OpenAI from 'openai';
import fs from 'fs';
import path from 'path';
import axios from 'axios';

const openai = new OpenAI({ apiKey: process.env.IMAGE_GEN_API_KEY });

/**
 * Generates one image per scene from its imagePrompt using OpenAI's image API.
 * Swap the `openai.images.generate` call for another provider (Flux via
 * Replicate, Imagen, SDXL self-hosted) if that turns out cheaper at scale --
 * the cost comparison from the plan flagged this as the main margin lever.
 */
export async function generateImages(scenes, jobId, outDir) {
  const imagePaths = [];

  for (let i = 0; i < scenes.length; i++) {
    const result = await openai.images.generate({
      model: 'dall-e-3',
      prompt: scenes[i].imagePrompt,
      size: '1024x1792', // portrait, matches short-form vertical video
      n: 1,
      response_format: 'url'
    });

    const imageUrl = result.data[0].url;
    const outPath = path.join(outDir, `scene_${i}.png`);

    const response = await axios.get(imageUrl, { responseType: 'arraybuffer' });
    fs.writeFileSync(outPath, response.data);

    imagePaths.push(outPath);
  }

  return imagePaths;
}
