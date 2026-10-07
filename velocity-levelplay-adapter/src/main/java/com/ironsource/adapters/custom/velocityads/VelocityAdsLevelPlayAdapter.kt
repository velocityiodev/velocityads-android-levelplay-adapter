package com.ironsource.adapters.custom.velocityads

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter
import com.ironsource.mediationsdk.adunit.adapter.internal.AdapterMetaDataInterface
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors
import com.unity3d.mediation.LevelPlay
import io.velocityads.sdk.VelocityAds
import io.velocityads.sdk.VelocityAdsMediationBridge
import io.velocityads.sdk.listeners.VelocityAdsInitListener
import io.velocityads.sdk.models.VelocityAdsError
import io.velocityads.sdk.models.VelocityAdsErrorCode
import io.velocityads.sdk.models.VelocityAdsInitRequest
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Unity LevelPlay custom adapter for Velocity Ads.
 *
 * The package and class name are registration placeholders until Unity assigns the final
 * reflection names.
 */
class VelocityAdsLevelPlayAdapter :
    BaseAdapter(),
    AdapterMetaDataInterface,
    FormatAdapterContext {
    companion object {
        private const val TAG = "VelocityLevelPlay"
        private const val MEDIATION_NAME = "levelplay"
        private const val DO_NOT_SELL_KEY = "do_not_sell"

        private val initCoalescer = InitCoalescer<Boolean>()
        private val mediationInfoForwarded = AtomicBoolean(false)

        @Volatile private var storedAppKey: String? = null

        @Volatile private var consent: Boolean? = null

        @Volatile private var doNotSell: Boolean? = null

        private fun forwardMediationInfo() {
            if (!mediationInfoForwarded.compareAndSet(false, true)) return
            VelocityAdsMediationBridge.setMediationInfo(
                MEDIATION_NAME,
                BuildConfig.ADAPTER_VERSION,
                LevelPlay.getSdkVersion(),
            )
        }
    }

    override fun init(
        adData: AdData,
        context: Context,
        listener: NetworkInitializationListener?,
    ) {
        forwardMediationInfo()
        forwardPrivacySettings()

        if (VelocityAds.isInitialized()) {
            runOnMain { listener?.onInitSuccess() }
            return
        }

        val appKey = VelocityAdsServerParameters.parse(adData).appKey
        if (appKey == null) {
            runOnMain {
                listener?.onInitFailed(
                    AdapterErrors.ADAPTER_ERROR_MISSING_PARAMS,
                    "Missing required LevelPlay configuration value: ${RegistrationConfig.APP_KEY}",
                )
            }
            return
        }
        storedAppKey = storedAppKey ?: appKey
        runOnMain {
            val won =
                initCoalescer.claim { initialized ->
                    runOnMain {
                        if (initialized) {
                            listener?.onInitSuccess()
                        } else {
                            listener?.onInitFailed(
                                AdapterErrors.ADAPTER_ERROR_INTERNAL,
                                "Velocity Ads initialization failed",
                            )
                        }
                    }
                }
            if (won) startClaimedInit(context.applicationContext, appKey)
        }
    }

    override fun ensureInitialized(
        adData: AdData,
        context: Context,
        onReady: (Boolean) -> Unit,
    ) {
        forwardMediationInfo()
        forwardPrivacySettings()
        if (VelocityAds.isInitialized()) {
            runOnMain { onReady(true) }
            return
        }

        val loadAppKey = VelocityAdsServerParameters.parse(adData).appKey
        if (loadAppKey != null && storedAppKey == null) storedAppKey = loadAppKey
        val appKey = loadAppKey ?: storedAppKey
        if (appKey == null) {
            runOnMain { onReady(false) }
            return
        }

        runOnMain {
            if (VelocityAds.isInitialized()) {
                onReady(true)
                return@runOnMain
            }
            val won =
                initCoalescer.claim { initialized ->
                    runOnMain { onReady(initialized) }
                }
            if (won) startClaimedInit(context.applicationContext, appKey)
        }
    }

    private fun startClaimedInit(
        context: Context,
        appKey: String,
    ) {
        val request = VelocityAdsInitRequest.Builder(appKey).build()
        try {
            VelocityAds.initSDK(
                context,
                request,
                object : VelocityAdsInitListener {
                    override fun onInitSuccess() {
                        completeOnMain(true)
                    }

                    override fun onInitFailure(error: VelocityAdsError) {
                        if (error.code == VelocityAdsErrorCode.SDK_INITIALIZATION_IN_PROGRESS) {
                            InFlightInitPoller.awaitInitialization(VelocityAds::isInitialized) {
                                completeOnMain(it)
                            }
                        } else {
                            Log.w(TAG, "Initialization failed [${error.code}]: ${error.message}")
                            completeOnMain(false)
                        }
                    }
                },
            )
        } catch (error: Throwable) {
            Log.e(TAG, "Initialization threw unexpectedly", error)
            completeOnMain(false)
        }
    }

    private fun completeOnMain(initialized: Boolean) {
        runOnMain { initCoalescer.complete(initialized) }
    }

    override fun getNetworkSDKVersion(): String = VelocityAds.getSdkVersion()

    override fun getAdapterVersion(): String = BuildConfig.ADAPTER_VERSION

    override fun setConsent(value: Boolean) {
        consent = value
        try {
            VelocityAds.setConsent(value)
        } catch (_: Exception) {
            // Privacy forwarding must never interrupt mediation.
        }
    }

    override fun setMetaData(
        key: String,
        values: MutableList<String>,
    ) {
        if (!key.equals(DO_NOT_SELL_KEY, ignoreCase = true)) return
        val value = values.firstOrNull()?.toBooleanStrictOrNull() ?: return
        doNotSell = value
        try {
            VelocityAds.setDoNotSell(value)
        } catch (_: Exception) {
            // Privacy forwarding must never interrupt mediation.
        }
    }

    override fun forwardPrivacySettings() {
        try {
            consent?.let(VelocityAds::setConsent)
            doNotSell?.let(VelocityAds::setDoNotSell)
        } catch (_: Exception) {
            // Privacy forwarding must never interrupt mediation.
        }
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post(block)
        }
    }
}
