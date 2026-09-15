import axios from 'axios';

const backend = axios.create({
  baseURL: process.env.BACKEND_BASE_URL,
  headers: { 'X-Internal-Key': process.env.INTERNAL_API_KEY }
});

export async function updateStatus(jobId, status) {
  await backend.patch(`/internal/videos/${jobId}/status`, null, { params: { status } });
}

export async function markComplete(jobId, videoUrl) {
  await backend.post(`/internal/videos/${jobId}/complete`, { videoUrl });
}

export async function markFailed(jobId, reason) {
  await backend.post(`/internal/videos/${jobId}/fail`, { reason });
}
