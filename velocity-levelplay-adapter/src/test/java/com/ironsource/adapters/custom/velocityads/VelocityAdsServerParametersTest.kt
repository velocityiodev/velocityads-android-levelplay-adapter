package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class VelocityAdsServerParametersTest {
    @Test
    fun `parser reads registration keys`() {
        val adData =
            AdData(
                "",
                mapOf(
                    RegistrationConfig.APP_KEY to " app ",
                    RegistrationConfig.AD_UNIT_ID to " unit ",
                ),
                emptyMap(),
            )

        val parsed = VelocityAdsServerParameters.parse(adData)

        assertEquals("app", parsed.appKey)
        assertEquals("unit", parsed.adUnitId)
    }

    @Test
    fun `parser normalizes blank and missing values`() {
        val adData = AdData("", emptyMap(), mapOf(RegistrationConfig.APP_KEY to " "))

        val parsed = VelocityAdsServerParameters.parse(adData)

        assertNull(parsed.appKey)
        assertNull(parsed.adUnitId)
    }
}
