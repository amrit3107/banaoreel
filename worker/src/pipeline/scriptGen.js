import Anthropic from '@anthropic-ai/sdk';

const client = new Anthropic({ apiKey: process.env.ANTHROPIC_API_KEY });

/**
 * Turns the user's free-text prompt into a scene-by-scene script:
 * [{ narration: string, imagePrompt: string }, ...]
 * Scene count/pacing should target the requested durationSec
 * (roughly one scene per 5-6 seconds of narration).
 */
export async function generateScript(prompt, durationSec) {
  const sceneCount = Math.max(2, Math.round(durationSec / 5));

  const systemPrompt = `You write short-form video scripts. Given a topic, produce exactly ${sceneCount} scenes. ` +
    `Respond ONLY with JSON: an array of {"narration": string, "imagePrompt": string}. ` +
    `Total narration should read aloud in about ${durationSec} seconds.`;

  const response = await client.messages.create({
    model: 'claude-sonnet-4-6',
    max_tokens: 1024,
    system: systemPrompt,
    messages: [{ role: 'user', content: prompt }]
  });

  const text = response.content.find(b => b.type === 'text')?.text ?? '[]';
  // TODO: wrap in try/catch and validate shape before trusting it downstream;
  // consider using tool-use / structured output instead of raw JSON-in-text.
  return JSON.parse(text);
}
