package mz.feng.smsclassifier.sms

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class InMemorySmsRepository(
    private val inboxReader: InboxSmsReader,
    private val store: InMemorySmsStore = InMemorySmsStore(),
) : SmsRepository {
    override val messages: StateFlow<List<IncomingSms>> = store.messages

    override suspend fun loadInbox() {
        val inbox = withContext(Dispatchers.IO) { inboxReader.read() }
        store.setInbox(inbox)
    }

    override fun addIncoming(sms: IncomingSms) {
        store.prepend(sms)
    }
}
