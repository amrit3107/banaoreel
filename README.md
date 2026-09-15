# BanaoReel — project scaffold

Three services, matching the plan discussed:

- `android/` — Kotlin + Jetpack Compose app (MVVM, Hilt, Retrofit)
- `backend/` — Java Spring Boot (auth, wallet, job lifecycle, Razorpay hooks)
- `worker/` — Node.js generation worker (Claude script -> image gen -> ElevenLabs TTS -> FFmpeg -> S3)

## What's real vs stubbed

This is a scaffold, not a finished app. Wired up and working end-to-end in structure:
- Wallet debit-and-create-job as one atomic transaction (backend)
- Job status state machine and auto-refund-on-failure (backend + worker)
- Full Compose screen flow: Home -> Create -> Job status (polling) -> Wallet
- Worker pipeline shape: queue -> script -> images -> TTS -> assemble -> upload -> callback

Marked with `TODO` and left as stubs (by design, since these need real credentials/decisions):
- OTP sending + real JWT auth (backend currently trusts an X-User-Id header — replace before any real use)
- Razorpay order creation + signature verification
- Actual image-gen and ElevenLabs API calls
- FFmpeg filter graph in assemble.js
- SQS queue wiring on the backend side (JobQueuePublisher is a stub)

## Suggested next steps
1. Get Postgres running locally, point `backend/src/main/resources/application.yml` at it, run the Spring Boot app — confirm the wallet/job tables create and the createJob->debit transaction works via Postman with a manual X-User-Id header.
2. Wire one real API call at a time in the worker (start with scriptGen.js against the real Anthropic API) before tackling FFmpeg.
3. Only after the backend + worker loop works end-to-end, connect the Android app to a real base URL.
