package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener
import io.velocityads.sdk.models.VelocityFullscreenAd
import org.junit.Test
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class VelocityRewardedAdHandlerTest {
    private val listener = mock(RewardedVideoAdListener::class.java)
    private val ad = mock(VelocityFullscreenAd::class.java)

    @Test
    fun `reward is forwarded before close`() {
        val handler = VelocityRewardedAdHandler(listener)

        handler.onUserRewarded(ad)
        handler.onAdDismissed(ad)

        inOrder(listener).apply {
            verify(listener).onAdRewarded()
            verify(listener).onAdClosed()
        }
    }

    @Test
    fun `shown maps to opened`() {
        VelocityRewardedAdHandler(listener).onAdShown(ad)

        verify(listener).onAdOpened()
    }
}
