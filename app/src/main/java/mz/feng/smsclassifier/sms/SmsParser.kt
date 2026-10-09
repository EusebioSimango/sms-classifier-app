package mz.feng.smsclassifier.sms

import android.content.Intent
import android.provider.Telephony

object SmsParser {
    fun fromReceivedIntent(intent: Intent): IncomingSms? {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return null
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return null
        return IncomingSms(
            sender = messages.first().originatingAddress.orEmpty(),
            body = messages.joinToString(separator = "") { it.messageBody.orEmpty() },
            receivedAtMillis = messages.first().timestampMillis,
        )
    }
}
