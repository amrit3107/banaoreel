import 'dotenv/config';
import fs from 'fs';
import { SQSClient, ReceiveMessageCommand, DeleteMessageCommand } from '@aws-sdk/client-sqs';
import { generateScript } from './pipeline/scriptGen.js';
import { generateImages } from './pipeline/imageGen.js';
import { generateNarration } from './pipeline/tts.js';
import { assembleVideo } from './pipeline/assemble.js';
import { uploadVideo } from './services/storage.js';
import { updateStatus, markComplete, markFailed } from './services/backendClient.js';

const sqs = new SQSClient({ region: process.env.AWS_REGION });

async function processJob(jobId, prompt, durationSec) {
  const outDir = `${process.env.TMP_DIR}/${jobId}`;
  fs.mkdirSync(outDir, { recursive: true });

  try {
    await updateStatus(jobId, 'SCRIPTING');
    const scenes = await generateScript(prompt, durationSec);

    await updateStatus(jobId, 'RENDERING_VISUALS');
    const imagePaths = await generateImages(scenes, jobId, outDir);

    await updateStatus(jobId, 'VOICING');
    const audioPaths = await generateNarration(scenes, jobId, outDir);

    await updateStatus(jobId, 'ASSEMBLING');
    const localVideoPath = await assembleVideo({ imagePaths, audioPaths, scenes, jobId, outDir });

    const videoUrl = await uploadVideo(jobId, localVideoPath);
    await markComplete(jobId, videoUrl);
  } catch (err) {
    console.error(`Job ${jobId} failed:`, err);
    // Marking failed here is what triggers the backend's auto-refund logic
    // (VideoJobService.markFailed -> WalletService.refund), per the product plan.
    await markFailed(jobId, err.message ?? 'Unknown error');
  } finally {
    fs.rmSync(outDir, { recursive: true, force: true });
  }
}

async function pollLoop() {
  console.log('BanaoReel worker started, polling queue...');
  while (true) {
    const { Messages } = await sqs.send(new ReceiveMessageCommand({
      QueueUrl: process.env.SQS_QUEUE_URL,
      MaxNumberOfMessages: 1,
      WaitTimeSeconds: 20
    }));

    if (!Messages || Messages.length === 0) continue;

    for (const message of Messages) {
      // TODO: message body currently assumed to be {jobId, prompt, durationSec} JSON;
      // confirm this matches whatever JobQueuePublisher on the backend actually sends.
      const { jobId, prompt, durationSec } = JSON.parse(message.Body);

      await processJob(jobId, prompt, durationSec);

      await sqs.send(new DeleteMessageCommand({
        QueueUrl: process.env.SQS_QUEUE_URL,
        ReceiptHandle: message.ReceiptHandle
      }));
    }
  }
}

pollLoop().catch(err => {
  console.error('Worker crashed:', err);
  process.exit(1);
});
