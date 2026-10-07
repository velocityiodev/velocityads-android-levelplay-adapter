package com.ironsource.adapters.custom.velocityads

import android.app.Activity
import android.content.Context
import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings
import io.velocityads.sdk.models.VelocityRewardedAd
import io.velocityads.sdk.models.VelocityRewardedAdRequest

class VelocityAdsLevelPlayRewardedVideo(
    networkSettings: NetworkSettings,
) : BaseRewardedVideo<VelocityAdsLevelPlayAdapter>(networkSettings) {
    private var ad: VelocityRewardedAd? = null
    private var handler: VelocityRewardedAdHandler? = null

    override fun loadAd(
        adData: AdData,
        context: Context,
        listener: RewardedVideoAdListener,
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
        adapter.ensureInitialized(adData, context) { initialized ->
            if (!initialized) {
                listener.onAdLoadFailed(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL,
                    "Velocity Ads is not initialized",
                )
                return@ensureInitialized
            }
            ad?.destroy()
            val newAd = VelocityRewardedAd(VelocityRewardedAdRequest.Builder(adUnitId).build())
            val newHandler =
                VelocityRewardedAdHandler(listener) {
                    if (ad === newAd) {
                        ad = null
                        handler = null
                    }
                }
            ad = newAd
            handler = newHandler
            newAd.load(newHandler)
        }
    }

    override fun showAd(
        adData: AdData,
        activity: Activity,
        listener: RewardedVideoAdListener,
    ) {
        val currentAd = ad
        val currentHandler = handler
        if (currentAd == null || currentHandler == null || !currentAd.isReady) {
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_AD_EXPIRED, "Rewarded ad is not ready")
            return
        }
        currentHandler.attachShowListener(listener)
        currentAd.show(activity)
    }

    override fun isAdAvailable(adData: AdData): Boolean = ad?.isReady == true

    override fun destroyAd(adData: AdData) {
        ad?.destroy()
        ad = null
        handler = null
    }
}
