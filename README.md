# BanaoReel — full app

Three services:
- `android/` — Kotlin + Jetpack Compose app
- `backend/` — Java Spring Boot API
- `worker/` — Node.js generation worker

## What's fully implemented (real logic, not stubs)

**Backend**
- Phone OTP auth with real JWT issuance/verification (JwtService, JwtAuthFilter) — OTP delivery itself uses a
  dev logger by default (LoggingSmsSender); swap in Msg91SmsSender (or another provider) for real SMS
- Wallet debit + job creation as one atomic transaction, with auto-refund on job failure
- Real Razorpay order creation, signature verification, and a webhook endpoint (signature check on the
  webhook itself is still a TODO — see comment in WalletController)
- Real SQS job publishing
- Real FCM push (Firebase Admin) sent on job completion/failure — needs a Firebase service account JSON dropped in
- Full CRUD for video jobs, wallet, transactions, internal worker callbacks

**Worker**
- Real Claude API call for script generation, with JSON validation
- Real OpenAI image generation call (dall-e-3) — swappable for a cheaper provider later, per the cost-margin
  discussion in the plan
- Real ElevenLabs TTS call
- Real FFmpeg pipeline: Ken Burns zoom per scene, burned-in captions, concat, optional background music mix
- Real SQS polling loop with backend status callbacks at every stage

**Android**
- Full screen flow: Login (OTP) -> Home -> Create -> Job status (polling) -> Preview (ExoPlayer video
  playback, download/share) -> Wallet -> History
- JWT stored via DataStore, attached to every request via an OkHttp interceptor
- Real Razorpay Android checkout SDK integration, wired through MainActivity's callback -> a shared event bus
  -> WalletViewModel (Razorpay's SDK only delivers results to an Activity, not to a composable directly)
- FCM token registration on the device, pushed to the backend

## What genuinely still needs you (not more scaffolding, but real accounts/keys/testing)

- **Credentials**: Anthropic, OpenAI (or your chosen image provider), ElevenLabs, Razorpay, AWS (SQS + S3),
  Firebase service account JSON, an SMS provider if you don't want dev-mode OTP logging
- **A first real run**: none of this has been executed against live services — get Postgres + the backend
  running locally first, confirm the debit-and-create-job flow via Postman, then bring the worker online one
  API at a time (script gen first, since it's cheapest to iterate on), then connect the Android app last
- **FFmpeg tuning**: the filter graph in `worker/src/pipeline/assemble.js` is a real, complete implementation,
  but caption line-wrapping and zoom pacing will need visual tuning once you can see actual output
- **Webhook signature verification** on the Razorpay webhook endpoint (flagged with a TODO in WalletController)
- **Play Store review** for AI-generated content policies, and the RBI wallet/PPI framing flagged earlier —
  neither is a coding task

## Note on verification

I syntax-checked all worker JS files (`node --check`, all pass) and brace-balanced every Kotlin/Java file (all
balanced) — but I could not actually compile the Android or Spring Boot projects in this environment (no
Maven Central or Google Maven access here), so there could still be a wrong import or type mismatch that only
a real build will catch. Run `./gradlew build` and `mvn compile` yourself as the first sanity check before
going further.
