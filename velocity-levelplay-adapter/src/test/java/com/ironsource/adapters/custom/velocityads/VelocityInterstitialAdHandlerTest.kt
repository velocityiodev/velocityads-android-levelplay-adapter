package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityAdsErrorCode
import io.velocityads.sdk.models.VelocityFullscreenAd
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

class VelocityInterstitialAdHandlerTest {
    private val listener = mock(InterstitialAdListener::class.java)
    private val ad = mock(VelocityFullscreenAd::class.java)

    @Test
    fun `forwards load show click and close callbacks`() {
        val handler = VelocityInterstitialAdHandler(listener)

        handler.onAdLoaded(ad)
        handler.onAdShown(ad)
        handler.onAdImpression(ad)
        handler.onAdClicked(ad)
        handler.onAdDismissed(ad)

        verify(listener).onAdLoadSuccess()
        verify(listener).onAdOpened()
        verify(listener).onAdClicked()
        verify(listener).onAdClosed()
        verify(listener, never()).onAdVisible()
    }

    @Test
    fun `forwards mapped load failure`() {
        val handler = VelocityInterstitialAdHandler(listener)

        handler.onAdFailedToLoad(ad, VelocityAdsError(VelocityAdsErrorCode.NO_FILL, "none"))

        verify(listener).onAdLoadFailed(
            com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,
            VelocityAdsErrorCode.NO_FILL,
            "Velocity Ads [${VelocityAdsErrorCode.NO_FILL}]: none",
        )
    }

    @Test
    fun `show listener replaces load listener`() {
        val showListener = mock(InterstitialAdListener::class.java)
        val handler = VelocityInterstitialAdHandler(listener)
        handler.attachShowListener(showListener)

        handler.onAdShown(ad)

        verify(showListener).onAdOpened()
        verify(listener, never()).onAdOpened()
    }
}
