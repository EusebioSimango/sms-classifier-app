package mz.smsclassifier.sms

class InboxSync(
    private val reader: InboxSmsReader,
    private val repository: SmsRepository,
) {
    suspend fun sync() {
        repository.upsertAll(reader.read())
    }
}
