package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener
import io.velocityads.sdk.listeners.VelocityInterstitialAdListener
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityFullscreenAd

internal class VelocityInterstitialAdHandler(
    private var listener: InterstitialAdListener,
    private val onDismissed: () -> Unit = {},
) : VelocityInterstitialAdListener {
    fun attachShowListener(listener: InterstitialAdListener) {
        this.listener = listener
    }

    override fun onAdLoaded(ad: VelocityFullscreenAd) = listener.onAdLoadSuccess()

    override fun onAdFailedToLoad(
        ad: VelocityFullscreenAd,
        error: VelocityAdsError,
    ) {
        val mapped = VelocityAdsErrorMapper.map(error)
        listener.onAdLoadFailed(mapped.type, mapped.code, mapped.message)
    }

    override fun onAdShown(ad: VelocityFullscreenAd) = listener.onAdOpened()

    override fun onAdImpression(ad: VelocityFullscreenAd) = Unit

    override fun onAdFailedToShow(
        ad: VelocityFullscreenAd,
        error: VelocityAdsError,
    ) {
        val mapped = VelocityAdsErrorMapper.map(error)
        listener.onAdShowFailed(mapped.code, mapped.message)
    }

    override fun onAdClicked(ad: VelocityFullscreenAd) = listener.onAdClicked()

    override fun onAdDismissed(ad: VelocityFullscreenAd) {
        listener.onAdClosed()
        onDismissed()
    }
}
