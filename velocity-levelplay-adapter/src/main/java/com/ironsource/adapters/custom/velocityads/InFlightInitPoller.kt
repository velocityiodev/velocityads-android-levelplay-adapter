package com.ironsource.adapters.custom.velocityads

import android.os.Handler
import android.os.Looper

internal object InFlightInitPoller {
    const val DEFAULT_POLL_INTERVAL_MS = 200L
    const val DEFAULT_TIMEOUT_MS = 5_000L

    fun awaitInitialization(
        isInitialized: () -> Boolean,
        pollIntervalMs: Long = DEFAULT_POLL_INTERVAL_MS,
        timeoutMs: Long = DEFAULT_TIMEOUT_MS,
        handler: Handler = Handler(Looper.getMainLooper()),
        onResult: (Boolean) -> Unit,
    ) {
        val polls = if (pollIntervalMs > 0) (timeoutMs / pollIntervalMs).toInt().coerceAtLeast(0) else 0
        poll(polls, isInitialized, pollIntervalMs, handler, onResult)
    }

    private fun poll(
        remainingPolls: Int,
        isInitialized: () -> Boolean,
        pollIntervalMs: Long,
        handler: Handler,
        onResult: (Boolean) -> Unit,
    ) {
        if (isInitialized()) {
            onResult(true)
            return
        }
        if (remainingPolls <= 0) {
            onResult(false)
            return
        }
        handler.postDelayed(
            { poll(remainingPolls - 1, isInitialized, pollIntervalMs, handler, onResult) },
            pollIntervalMs,
        )
    }
}
