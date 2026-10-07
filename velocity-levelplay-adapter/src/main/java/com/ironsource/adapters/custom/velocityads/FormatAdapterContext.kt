package com.ironsource.adapters.custom.velocityads

import android.content.Context
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData

internal interface FormatAdapterContext {
    fun ensureInitialized(
        adData: AdData,
        context: Context,
        onReady: (Boolean) -> Unit,
    )

    fun forwardPrivacySettings()
}
