package com.ironsource.adapters.custom.velocityads

import android.app.Activity
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.adunit.adapter.BaseBanner
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings

class VelocityAdsLevelPlayBanner(
    networkSettings: NetworkSettings,
) : BaseBanner<VelocityAdsLevelPlayAdapter>(networkSettings) {
    private var handler: VelocityBannerAdHandler? = null

    override fun loadAd(
        adData: AdData,
        activity: Activity,
        bannerSize: ISBannerSize,
        listener: BannerAdListener,
    ) {
        val adUnitId = VelocityAdsServerParameters.parse(adData).adUnitId
        if (adUnitId == null) {
            val error = VelocityAdsErrorMapper.missingParameter(RegistrationConfig.AD_UNIT_ID)
            listener.onAdLoadFailed(error.type, error.code, error.message)
            return
        }
        val adapter = networkAdapter
        if (adapter == null) {
            listener.onAdLoadFailed(
                AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                AdapterErrors.ADAPTER_ERROR_INTERNAL,
                "Velocity Ads LevelPlay adapter is unavailable",
            )
            return
        }
        adapter.forwardPrivacySettings()
        adapter.ensureInitialized(adData, activity) { initialized ->
            if (!initialized) {
                listener.onAdLoadFailed(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL,
                    "Velocity Ads is not initialized",
                )
                return@ensureInitialized
            }
            handler?.destroy()
            VelocityBannerAdHandler().also {
                handler = it
                it.load(adUnitId, activity, bannerSize, listener)
            }
        }
    }

    override fun destroyAd(adData: AdData) {
        handler?.destroy()
        handler = null
    }
}
