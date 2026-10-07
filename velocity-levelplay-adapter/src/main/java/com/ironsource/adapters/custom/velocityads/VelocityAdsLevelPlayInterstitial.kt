package com.ironsource.adapters.custom.velocityads

import android.app.Activity
import android.content.Context
import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.ironsource.mediationsdk.model.NetworkSettings
import io.velocityads.sdk.models.VelocityInterstitialAd
import io.velocityads.sdk.models.VelocityInterstitialAdRequest

class VelocityAdsLevelPlayInterstitial(
    networkSettings: NetworkSettings,
) : BaseInterstitial<VelocityAdsLevelPlayAdapter>(networkSettings) {
    private var ad: VelocityInterstitialAd? = null
    private var handler: VelocityInterstitialAdHandler? = null
    private var loadGeneration = 0L

    override fun loadAd(
        adData: AdData,
        context: Context,
        listener: InterstitialAdListener,
    ) {
        val requestGeneration = ++loadGeneration
        val adUnitId = VelocityAdsServerParameters.parse(adData).adUnitId
        if (adUnitId == null) {
            val error = VelocityAdsErrorMapper.missingParameter(RegistrationConfig.AD_UNIT_ID)
            listener.onAdLoadFailed(error.type, error.code, error.message)
            return
        }
        val adapter = networkAdapter
        if (adapter == null) {
            listener.onAdLoadFailed(
                com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                AdapterErrors.ADAPTER_ERROR_INTERNAL,
                "Velocity Ads LevelPlay adapter is unavailable",
            )
            return
        }
        adapter.forwardPrivacySettings()
        adapter.ensureInitialized(adData, context) { initialized ->
            if (requestGeneration != loadGeneration) return@ensureInitialized
            if (!initialized) {
                listener.onAdLoadFailed(
                    com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL,
                    "Velocity Ads is not initialized",
                )
                return@ensureInitialized
            }
            ad?.destroy()
            val newAd = VelocityInterstitialAd(VelocityInterstitialAdRequest.Builder(adUnitId).build())
            val newHandler =
                VelocityInterstitialAdHandler(listener) {
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
        listener: InterstitialAdListener,
    ) {
        val currentAd = ad
        val currentHandler = handler
        if (currentAd == null || currentHandler == null || !currentAd.isReady) {
            listener.onAdShowFailed(AdapterErrors.ADAPTER_ERROR_AD_EXPIRED, "Interstitial ad is not ready")
            return
        }
        currentHandler.attachShowListener(listener)
        currentAd.show(activity)
    }

    override fun isAdAvailable(adData: AdData): Boolean = ad?.isReady == true

    override fun destroyAd(adData: AdData) {
        loadGeneration += 1
        ad?.destroy()
        ad = null
        handler = null
    }
}
