package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.utility.AdData

internal data class VelocityAdsServerParameters(
    val appKey: String?,
    val adUnitId: String?,
) {
    companion object {
        val EMPTY = VelocityAdsServerParameters(null, null)

        fun parse(adData: AdData?): VelocityAdsServerParameters {
            if (adData == null) return EMPTY
            return VelocityAdsServerParameters(
                appKey = adData.getString(RegistrationConfig.APP_KEY)?.trim()?.takeUnless { it.isEmpty() },
                adUnitId = adData.getString(RegistrationConfig.AD_UNIT_ID)?.trim()?.takeUnless { it.isEmpty() },
            )
        }
    }
}
