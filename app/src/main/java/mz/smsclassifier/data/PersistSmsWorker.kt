package mz.smsclassifier.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import mz.smsclassifier.SmsClassifierApp
import mz.smsclassifier.sms.IncomingSmsNotifier

class PersistSmsWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as SmsClassifierApp
            app.inboxSync.sync()
            app.classifyPendingSms()
            IncomingSmsNotifier.refreshCategories(
                applicationContext,
                app.smsRepository.messages.first(),
            )
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_NAME = "persist_sms"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<PersistSmsWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }
}
