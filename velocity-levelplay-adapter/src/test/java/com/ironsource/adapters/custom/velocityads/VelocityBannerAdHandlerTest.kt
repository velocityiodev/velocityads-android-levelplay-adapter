package com.ironsource.adapters.custom.velocityads

import android.app.Activity
import android.view.Gravity
import android.view.View
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.adunit.adapter.listener.BannerAdListener
import io.velocityads.sdk.ads.banner.VelocityBannerAdView
import io.velocityads.sdk.models.VelocityBannerAd
import io.velocityads.sdk.models.VelocityBannerAdSize
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class VelocityBannerAdHandlerTest {
    private lateinit var activity: Activity
    private lateinit var handler: VelocityBannerAdHandler

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java).get()
        handler = VelocityBannerAdHandler()
    }

    @Test
    fun `maps standard LevelPlay sizes`() {
        assertEquals(VelocityBannerAdSize.BANNER, handler.resolveAdSize(ISBannerSize.BANNER, activity))
        assertEquals(VelocityBannerAdSize.MREC, handler.resolveAdSize(ISBannerSize.RECTANGLE, activity))
        assertEquals(VelocityBannerAdSize.custom(320, 90), handler.resolveAdSize(ISBannerSize.LARGE, activity))
        assertEquals(
            VelocityBannerAdSize.LEADERBOARD,
            handler.resolveAdSize(ISBannerSize(728, 90), activity),
        )
    }

    @Test
    fun `smart size resolves from screen width`() {
        activity.resources.configuration.screenWidthDp = 320
        assertEquals(VelocityBannerAdSize.BANNER, handler.resolveAdSize(ISBannerSize.SMART, activity))

        activity.resources.configuration.screenWidthDp = 800
        assertEquals(VelocityBannerAdSize.LEADERBOARD, handler.resolveAdSize(ISBannerSize.SMART, activity))
    }

    @Test
    fun `adaptive size uses screen width`() {
        val requested = ISBannerSize.BANNER
        requested.setAdaptive(true)
        activity.resources.configuration.screenWidthDp = 400

        assertEquals(
            VelocityBannerAdSize.getAdaptiveBannerAdSize(activity, 400),
            handler.resolveAdSize(requested, activity),
        )
    }

    @Test
    fun `loaded banner returns centered layout params`() {
        val listener = mock(BannerAdListener::class.java)
        val view = VelocityBannerAdView(activity)
        val velocityListener = handler.createListener(view, listener)

        velocityListener.onAdLoaded(mock(VelocityBannerAd::class.java))

        val viewCaptor = ArgumentCaptor.forClass(View::class.java)
        val paramsCaptor = ArgumentCaptor.forClass(android.widget.FrameLayout.LayoutParams::class.java)
        verify(listener).onAdLoadSuccess(viewCaptor.capture(), paramsCaptor.capture())
        assertEquals(view, viewCaptor.value)
        assertEquals(Gravity.CENTER, paramsCaptor.value.gravity)
    }
}
