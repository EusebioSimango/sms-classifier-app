package mz.feng.smsclassifier.ui

import mz.feng.smsclassifier.sms.IncomingSms
import mz.feng.smsclassifier.sms.categorize
import kotlin.math.roundToInt

/** Label shown on a message. A missing confidence means the heuristic, not the model. */
fun classificationCaption(sms: IncomingSms): String {
    val category = sms.category ?: categorize(sms)
    val detail = sms.confidence?.let { "${(it * 100).roundToInt()}%" } ?: "provisória"
    return "${category.label} · $detail"
}
