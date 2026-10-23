package mz.feng.smsclassifier.classifier

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import mz.feng.smsclassifier.sms.SmsCategory

/** The ONNX graph embeds its own TF-IDF, so it must receive the raw SMS body. */
class OnnxSmsClassifier(context: Context) : SmsClassifier {
    private val appContext = context.applicationContext
    private val environment: OrtEnvironment by lazy {
        OrtEnvironment.getEnvironment().also { it.setTelemetry(false) }
    }
    private val session: OrtSession by lazy {
        val bytes = appContext.assets.open(ModelInfo.ASSET_PATH).use { it.readBytes() }
        environment.createSession(bytes, OrtSession.SessionOptions())
    }

    override fun classify(texts: List<String>): List<Classification> {
        if (texts.isEmpty()) return emptyList()
        val shape = longArrayOf(texts.size.toLong(), 1L)
        return OnnxTensor.createTensor(environment, texts.toTypedArray(), shape).use { input ->
            session.run(mapOf(ModelInfo.INPUT_NAME to input)).use { result ->
                @Suppress("UNCHECKED_CAST")
                val labels = result.get(0).value as Array<String>
                @Suppress("UNCHECKED_CAST")
                val scores = result.get(1).value as Array<FloatArray>
                labels.indices.map { i ->
                    Classification(
                        category = SmsCategory.fromModelLabel(labels[i]),
                        confidence = confidenceOf(scores[i]),
                        modelVersion = ModelInfo.VERSION,
                    )
                }
            }
        }
    }
}
