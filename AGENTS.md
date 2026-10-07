# Engineering guide

## Public repository

This repository is public. Keep all source, documentation, comments, workflow text, and
commit messages publisher-safe.

Do not include credentials, unpublished product plans, private service details, private
repository names, or maintainer-only release procedures.

## Project

This library is the Unity LevelPlay custom adapter for the Velocity Ads Android SDK.

- Maven artifact: `io.velocity:levelplay-mediation`
- Version: `VERSION_NAME` in `gradle.properties`
- Minimum Android API: 24
- Adapter module: `velocity-levelplay-adapter`
- LevelPlay dependency: `com.unity3d.ads-mediation:mediation-sdk`

The adapter version uses four segments:
`<Velocity major>.<Velocity minor>.<Velocity patch>.<adapter build>`.

## Registration rename checklist

The initial source uses registration placeholders. When Unity supplies the final custom
network registration, update all of these together:

1. Package name in source files and the Android namespace.
2. Base adapter, interstitial, rewarded-video, and banner class names.
3. Keep rules in `consumer-rules.pro` and `proguard-rules.pro`.
4. `RegistrationConfig.APP_KEY` and `RegistrationConfig.AD_UNIT_ID`.
5. Parser tests and all reflection/configuration values in `README.md`.
6. Search the repository for `placeholder`, `custom.velocityads`, `appKey`, and `adUnitId`.

Do not release while placeholder values remain.

## Architecture

`VelocityAdsLevelPlayAdapter` extends LevelPlay `BaseAdapter` and owns process-wide SDK
initialization. `InitCoalescer` ensures repeated LevelPlay initialization calls share one
attempt. `InFlightInitPoller` handles an initialization already started by the host app.

Each format has one LevelPlay reflection entry point and one callback translator:

- `VelocityAdsLevelPlayInterstitial` / `VelocityInterstitialAdHandler`
- `VelocityAdsLevelPlayRewardedVideo` / `VelocityRewardedAdHandler`
- `VelocityAdsLevelPlayBanner` / `VelocityBannerAdHandler`

LevelPlay creates the four entry-point classes by name. Keep them public and preserve their
consumer keep rules. Keep implementation helpers `internal`.

## Build and test

```bash
./gradlew :velocity-levelplay-adapter:ktlintCheck
./gradlew :velocity-levelplay-adapter:build
```

Before changing the LevelPlay dependency, inspect the official artifact signatures and the
Unity custom-adapter documentation. Do not infer callback contracts from another mediation
SDK.

## Release checks

1. The build and unit tests pass.
2. `CHANGELOG.md` describes publisher-visible changes.
3. `README.md` matches the public integration contract.
4. Registration placeholders have been replaced with Unity-assigned values.
5. No private implementation or operational details appear in committed files.
