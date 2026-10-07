package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener
import io.velocityads.sdk.listeners.VelocityRewardedAdListener
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityFullscreenAd

internal class VelocityRewardedAdHandler(
    private var listener: RewardedVideoAdListener,
    private val onDismissed: () -> Unit = {},
) : VelocityRewardedAdListener {
    fun attachShowListener(listener: RewardedVideoAdListener) {
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

    override fun onUserRewarded(ad: VelocityFullscreenAd) = listener.onAdRewarded()

    override fun onAdDismissed(ad: VelocityFullscreenAd) {
        listener.onAdClosed()
        onDismissed()
    }
}
