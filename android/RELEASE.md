# Releasing BanaoReel

## 1. Generate a release keystore (one-time)

```bash
keytool -genkey -v -keystore banaoreel-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias banaoreel
```

You'll be prompted for a store password, a key password, and your
name/organization details (shown to users during install on some Android
versions, so use your real company name if you have one).

**Back this file up somewhere safe outside this repo** (password manager,
encrypted drive). If you lose it, you can never publish an update to the same
app listing again -- Google Play requires the same signing key for every
update.

## 2. Configure signing

```bash
cp android/app/keystore.properties.example android/app/keystore.properties
```

Edit `android/app/keystore.properties` with the real path and passwords from
step 1. This file is gitignored on purpose -- never commit it.

## 3. Point the release build at your real backend

In `android/app/build.gradle.kts`, the `release` build type's `BASE_URL`
still says `https://api.banaoreel.com/` -- a placeholder. Update it to
wherever you actually deploy the Spring Boot backend, and make sure that
backend is served over HTTPS (a plain HTTP production backend will fail
outright, since only your debug LAN IP is allow-listed for cleartext traffic
in `network_security_config.xml`).

## 4. Build

```bash
cd android
./gradlew bundleRelease   # produces an .aab for Play Store upload
# or
./gradlew assembleRelease # produces a signed, installable .apk
```

Output lands in `app/build/outputs/bundle/release/` or
`app/build/outputs/apk/release/`.

## 5. Before you actually submit to Play Store

None of this is code -- flagging so it doesn't get missed:

- **RBI wallet regulations**: frame in-app balance as non-transferable,
  non-refundable "credits" rather than a cash wallet (see the original
  product planning discussion) to avoid PPI licensing requirements.
- **Generative AI content policy**: Play Store reviews AI-content apps more
  closely than typical apps -- read their current policy before submitting.
- **Privacy policy**: required for any app handling phone numbers/payments.
  You'll need a hosted privacy policy URL for the Play Console listing.
- **Content moderation**: the backend doesn't yet filter prompts beyond
  whatever the Claude API itself refuses -- worth adding your own layer
  before opening this to the public, per the original plan's notes.
