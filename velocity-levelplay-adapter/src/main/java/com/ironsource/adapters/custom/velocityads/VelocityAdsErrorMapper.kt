package com.ironsource.adapters.custom.velocityads

import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityAdsErrorCode

internal data class LevelPlayAdapterError(
    val type: AdapterErrorType,
    val code: Int,
    val message: String,
)

internal object VelocityAdsErrorMapper {
    fun map(error: VelocityAdsError): LevelPlayAdapterError {
        val message = "Velocity Ads [${error.code}]: ${error.message}"
        return when (error.code) {
            VelocityAdsErrorCode.NO_FILL ->
                LevelPlayAdapterError(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL,
                    error.code,
                    message,
                )

            VelocityAdsErrorCode.AD_SPENT ->
                LevelPlayAdapterError(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_AD_EXPIRED,
                    AdapterErrors.ADAPTER_ERROR_AD_EXPIRED,
                    message,
                )

            VelocityAdsErrorCode.INVALID_APP_KEY,
            VelocityAdsErrorCode.INVALID_AD_UNIT_ID,
            ->
                LevelPlayAdapterError(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                    message,
                )

            else ->
                LevelPlayAdapterError(
                    AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
                    AdapterErrors.ADAPTER_ERROR_INTERNAL,
                    message,
                )
        }
    }

    fun missingParameter(name: String): LevelPlayAdapterError =
        LevelPlayAdapterError(
            AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL,
            AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
            "Missing required LevelPlay configuration value: $name",
        )
}
