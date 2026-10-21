package mz.feng.smsclassifier.classifier

import kotlin.math.exp

/**
 * LinearSVC returns raw margins, not probabilities. Softmax turns them into a
 * relative confidence in [0, 1]; it is not a calibrated probability.
 */
fun softmax(scores: FloatArray): FloatArray {
    if (scores.isEmpty()) return scores
    val max = scores.max()
    val exps = scores.map { exp((it - max).toDouble()) }
    val sum = exps.sum()
    return FloatArray(scores.size) { (exps[it] / sum).toFloat() }
}

fun confidenceOf(scores: FloatArray): Float? = softmax(scores).maxOrNull()
