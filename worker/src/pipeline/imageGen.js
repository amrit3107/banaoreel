import axios from 'axios';

/**
 * Generates one image per scene from its imagePrompt.
 * TODO: pick and wire an actual provider (Flux/SDXL via Replicate or Imagen)
 * based on the cost comparison from the plan -- cheaper models protect margin
 * at the Rs 5-10/video price point.
 */
export async function generateImages(scenes, jobId) {
  const imagePaths = [];
  for (let i = 0; i < scenes.length; i++) {
    // TODO: replace with real image-gen API call, save result to `${TMP_DIR}/${jobId}/scene_${i}.png`
    imagePaths.push(`/tmp/banaoreel-render/${jobId}/scene_${i}.png`);
  }
  return imagePaths;
}
