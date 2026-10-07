package com.ironsource.adapters.custom.velocityads

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener
import io.velocityads.sdk.ads.banner.VelocityBannerAdView
import io.velocityads.sdk.listeners.VelocityBannerAdListener
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityBannerAd
import io.velocityads.sdk.models.VelocityBannerAdRequest
import io.velocityads.sdk.models.VelocityBannerAdSize

internal class VelocityBannerAdHandler {
    @Volatile private var bannerAd: VelocityBannerAd? = null

    fun load(
        adUnitId: String,
        activity: Activity,
        requestedSize: ISBannerSize,
        listener: BannerAdListener,
    ) {
        val size = resolveAdSize(requestedSize, activity)
        val view = VelocityBannerAdView(activity)
        val ad = VelocityBannerAd(VelocityBannerAdRequest.Builder(adUnitId, size).build())
        bannerAd = ad
        ad.load(view, createListener(view, listener))
    }

    internal fun resolveAdSize(
        requestedSize: ISBannerSize,
        context: Context,
    ): VelocityBannerAdSize {
        val width = requestedSize.width
        val height = requestedSize.height
        return when {
            requestedSize.isSmart -> {
                if (context.resources.configuration.screenWidthDp >= VelocityBannerAdSize.LEADERBOARD.widthDp) {
                    VelocityBannerAdSize.LEADERBOARD
                } else {
                    VelocityBannerAdSize.BANNER
                }
            }

            requestedSize.isAdaptive -> {
                val availableWidth = context.resources.configuration.screenWidthDp.coerceAtLeast(1)
                VelocityBannerAdSize.getAdaptiveBannerAdSize(context, availableWidth)
            }

            width == ISBannerSize.BANNER.width && height == ISBannerSize.BANNER.height ->
                VelocityBannerAdSize.BANNER

            width == ISBannerSize.RECTANGLE.width && height == ISBannerSize.RECTANGLE.height ->
                VelocityBannerAdSize.MREC

            width == 728 && height == 90 -> VelocityBannerAdSize.LEADERBOARD
            else -> VelocityBannerAdSize.custom(width, height)
        }
    }

    internal fun createListener(
        view: VelocityBannerAdView,
        listener: BannerAdListener,
    ): VelocityBannerAdListener =
        object : VelocityBannerAdListener {
            override fun onAdLoaded(ad: VelocityBannerAd) {
                val layoutParams =
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER,
                    )
                listener.onAdLoadSuccess(view, layoutParams)
            }

            override fun onAdFailedToLoad(
                ad: VelocityBannerAd,
                error: VelocityAdsError,
            ) {
                val mapped = VelocityAdsErrorMapper.map(error)
                listener.onAdLoadFailed(mapped.type, mapped.code, mapped.message)
            }

            override fun onAdImpression(ad: VelocityBannerAd) = listener.onAdOpened()

            override fun onAdClicked(ad: VelocityBannerAd) = listener.onAdClicked()

            override fun onAdFailedToShow(
                ad: VelocityBannerAd,
                error: VelocityAdsError,
            ) {
                val mapped = VelocityAdsErrorMapper.map(error)
                listener.onAdShowFailed(mapped.code, mapped.message)
            }
        }

    fun destroy() {
        bannerAd?.destroy()
        bannerAd = null
    }
}
