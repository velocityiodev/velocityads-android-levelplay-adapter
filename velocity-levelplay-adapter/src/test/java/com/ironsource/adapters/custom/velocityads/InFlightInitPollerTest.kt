package com.ironsource.adapters.custom.velocityads

import android.os.Handler
import android.os.Looper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class InFlightInitPollerTest {
    private val handler = Handler(Looper.getMainLooper())

    @Test
    fun `reports success when initialization becomes ready`() {
        var initialized = false
        val results = mutableListOf<Boolean>()
        InFlightInitPoller.awaitInitialization(
            isInitialized = { initialized },
            pollIntervalMs = 100,
            timeoutMs = 500,
            handler = handler,
            onResult = results::add,
        )

        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200))
        assertTrue(results.isEmpty())
        initialized = true
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(100))

        assertEquals(listOf(true), results)
    }

    @Test
    fun `reports failure once after timeout`() {
        val results = mutableListOf<Boolean>()
        InFlightInitPoller.awaitInitialization(
            isInitialized = { false },
            pollIntervalMs = 100,
            timeoutMs = 300,
            handler = handler,
            onResult = results::add,
        )

        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1_000))
        assertEquals(listOf(false), results)
    }
}
