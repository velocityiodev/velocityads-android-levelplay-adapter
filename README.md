# Velocity Ads Unity LevelPlay adapter for Android

This adapter lets publishers use Velocity Ads as a custom network in Unity LevelPlay.

> [!IMPORTANT]
> Unity custom-network registration is still pending. The package, reflection class names,
> and configuration keys shown below are placeholders. Do not publish or configure this
> adapter in production until Unity assigns the final values.

## Requirements

- Android API 24 or later
- Velocity Ads SDK 0.11.0
- Unity LevelPlay SDK 9.6.1

## Install

Add Maven Central:

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

Add the adapter and LevelPlay SDK:

```gradle
dependencies {
    implementation "io.velocity:levelplay-mediation:0.11.0.0"
    implementation "com.unity3d.ads-mediation:mediation-sdk:9.6.1"
}
```

The adapter brings in `io.velocity:ads-sdk:0.11.0` transitively.

### Unity dependency resolver

For a Unity Android export, add the equivalent dependencies to your EDM4U XML:

```xml
<dependencies>
  <androidPackages>
    <androidPackage spec="io.velocity:levelplay-mediation:0.11.0.0" />
    <androidPackage spec="com.unity3d.ads-mediation:mediation-sdk:9.6.1" />
  </androidPackages>
</dependencies>
```

## LevelPlay custom-network configuration

The current placeholder reflection values are:

| Role | Placeholder value |
|---|---|
| Package | `com.ironsource.adapters.custom.velocityads` |
| Base adapter | `VelocityAdsLevelPlayAdapter` |
| Interstitial | `VelocityAdsLevelPlayInterstitial` |
| Rewarded video | `VelocityAdsLevelPlayRewardedVideo` |
| Banner | `VelocityAdsLevelPlayBanner` |
| App-level key | `appKey` |
| Instance-level key | `adUnitId` |

Configure the Velocity app key once at app level and the matching Velocity ad unit ID on
each LevelPlay instance.

Supported formats:

- Interstitial
- Rewarded
- Banner, large banner, MREC, leaderboard, smart, adaptive, and custom banner sizes

Native ads are not supported.

## Privacy

Set privacy values through LevelPlay before initialization. LevelPlay forwards consent to
the adapter. The adapter also accepts the standard `do_not_sell` metadata value and applies
updated values before each ad load.

```kotlin
LevelPlay.setConsent(true)
LevelPlay.setMetaData("do_not_sell", "true")
```

`do_not_sell=true` means the user opted out of sale or sharing.

## Versioning

Adapter versions use four segments:
`<Velocity major>.<Velocity minor>.<Velocity patch>.<adapter build>`.

| Adapter | Velocity Ads SDK | LevelPlay SDK |
|---|---|---|
| 0.11.0.0 | 0.11.0 | 9.6.1 |

## License

Apache License 2.0. See [LICENSE](LICENSE).