package mz.smsclassifier.sms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat

data class SimLine(
    val subscriptionId: Int,
    val slot: Int,
    val carrier: String,
) {
    fun label(): String {
        val which = "SIM ${slot + 1}"
        return if (carrier.isBlank()) which else "$which · $carrier"
    }
}

class SimDirectory(context: Context) {
    private val appContext = context.applicationContext

    fun lines(): List<SimLine> {
        val granted = ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_PHONE_STATE) ==
            PackageManager.PERMISSION_GRANTED
        if (!granted) return emptyList()
        val manager = appContext.getSystemService(SubscriptionManager::class.java) ?: return emptyList()
        return manager.activeSubscriptionInfoList.orEmpty()
            .map { info ->
                SimLine(
                    subscriptionId = info.subscriptionId,
                    slot = info.simSlotIndex,
                    carrier = info.carrierName?.toString()?.trim().orEmpty(),
                )
            }
            .sortedBy { it.slot }
    }
}

object SimChoice {
    private const val PREFS = "ui"
    private const val KEY = "sms_subscription_id"

    fun savedId(context: Context): Int? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY)) return null
        return prefs.getInt(KEY, SubscriptionManager.INVALID_SUBSCRIPTION_ID)
    }

    fun save(context: Context, subscriptionId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY, subscriptionId)
            .apply()
    }

    fun resolve(context: Context, lines: List<SimLine>): Int? {
        if (lines.size < 2) return null
        val saved = savedId(context)
        if (saved != null && lines.any { it.subscriptionId == saved }) return saved
        val defaultId = SubscriptionManager.getDefaultSmsSubscriptionId()
        return lines.firstOrNull { it.subscriptionId == defaultId }?.subscriptionId
            ?: lines.first().subscriptionId
    }
}
