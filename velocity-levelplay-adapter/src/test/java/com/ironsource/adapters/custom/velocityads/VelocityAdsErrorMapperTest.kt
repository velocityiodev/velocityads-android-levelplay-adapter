package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityAdsErrorCode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VelocityAdsErrorMapperTest {
    @Test
    fun `no fill preserves its category and Velocity code`() {
        val mapped = VelocityAdsErrorMapper.map(VelocityAdsError(VelocityAdsErrorCode.NO_FILL, "none"))

        assertEquals(AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL, mapped.type)
        assertEquals(VelocityAdsErrorCode.NO_FILL, mapped.code)
        assertTrue(mapped.message.contains("${VelocityAdsErrorCode.NO_FILL}"))
    }

    @Test
    fun `spent maps to ad expired`() {
        val mapped = VelocityAdsErrorMapper.map(VelocityAdsError(VelocityAdsErrorCode.AD_SPENT, "spent"))

        assertEquals(AdapterErrorType.ADAPTER_ERROR_TYPE_AD_EXPIRED, mapped.type)
        assertEquals(AdapterErrors.ADAPTER_ERROR_AD_EXPIRED, mapped.code)
    }

    @Test
    fun `invalid configuration maps to missing params`() {
        val codes = listOf(VelocityAdsErrorCode.INVALID_APP_KEY, VelocityAdsErrorCode.INVALID_AD_UNIT_ID)
        codes.forEach { code ->
            val mapped = VelocityAdsErrorMapper.map(VelocityAdsError(code, "invalid"))
            assertEquals(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, mapped.type)
            assertEquals(AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS, mapped.code)
        }
    }

    @Test
    fun `waterfall failure remains internal rather than no fill`() {
        val mapped =
            VelocityAdsErrorMapper.map(
                VelocityAdsError(VelocityAdsErrorCode.WATERFALL_LOAD_FAILED, "winner failed"),
            )

        assertEquals(AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL, mapped.type)
        assertEquals(AdapterErrors.ADAPTER_ERROR_INTERNAL, mapped.code)
    }
}
