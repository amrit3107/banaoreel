import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import fs from 'fs';

const s3 = new S3Client({ region: process.env.AWS_REGION });

export async function uploadVideo(jobId, localPath) {
  const key = `videos/${jobId}.mp4`;
  const body = fs.readFileSync(localPath);

  await s3.send(new PutObjectCommand({
    Bucket: process.env.S3_BUCKET,
    Key: key,
    Body: body,
    ContentType: 'video/mp4'
  }));

  // TODO: return a CDN URL (e.g. CloudFront domain) instead of raw S3 URL
  return `https://${process.env.S3_BUCKET}.s3.${process.env.AWS_REGION}.amazonaws.com/${key}`;
}
