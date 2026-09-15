import Anthropic from '@anthropic-ai/sdk';

const client = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });

/**
 * Turns the user's free-text prompt into a scene-by-scene script:
 * [{ narration: string, imagePrompt: string }, ...]
 */
export async function generateScript(prompt, durationSec) {
  const sceneCount = Math.max(2, Math.round(durationSec / 5));

  const systemPrompt =
    `You write short-form video scripts. Given a topic, produce exactly ${sceneCount} scenes. ` +
    `Respond with ONLY a JSON array, no prose, no markdown fences: ` +
    `[{"narration": "...", "imagePrompt": "..."}]. ` +
    `Total narration should read aloud in about ${durationSec} seconds (roughly ${Math.round(durationSec * 2.5)} words total). ` +
    `imagePrompt should describe a single still visual for that scene, safe-for-work, no text/logos in the image.`;

  const response = await client.messages.create({
    model: 'claude-sonnet-4-6',
    max_tokens: 1024,
    system: systemPrompt,
    messages: [{ role: 'user', content: prompt }]
  });

  const text = response.content.find(b => b.type === 'text')?.text ?? '[]';

  let scenes;
  try {
    // Model occasionally wraps JSON in ```json fences despite instructions — strip if present.
    const cleaned = text.trim().replace(/^```json\s*/i, '').replace(/```\s*$/, '');
    scenes = JSON.parse(cleaned);
  } catch (err) {
    throw new Error(`Script generation returned invalid JSON: ${err.message}`);
  }

  if (!Array.isArray(scenes) || scenes.length === 0) {
    throw new Error('Script generation returned no scenes');
  }
  for (const scene of scenes) {
    if (!scene.narration || !scene.imagePrompt) {
      throw new Error('Script generation returned a scene missing narration or imagePrompt');
    }
  }

  return scenes;
}
