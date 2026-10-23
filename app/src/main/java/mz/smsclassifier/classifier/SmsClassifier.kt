package mz.smsclassifier.classifier

import mz.smsclassifier.sms.SmsCategory

data class Classification(
    val category: SmsCategory,
    val confidence: Float?,
    val modelVersion: String,
)

fun interface SmsClassifier {
    fun classify(texts: List<String>): List<Classification>
}
