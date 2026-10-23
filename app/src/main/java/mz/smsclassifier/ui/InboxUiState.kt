package mz.smsclassifier.ui

import mz.smsclassifier.settings.ThemeMode
import mz.smsclassifier.sms.SavedContact
import mz.smsclassifier.sms.SenderThread
import mz.smsclassifier.sms.SmsCategory
import mz.smsclassifier.sms.categorizeThread
import mz.smsclassifier.sms.containsFolded
import mz.smsclassifier.sms.nameFor
import mz.smsclassifier.sms.photoFor

data class CategorySummary(
    val category: SmsCategory,
    val count: Int,
    val lastAtMillis: Long?,
)

data class InboxUiState(
    val hasPermission: Boolean = false,
    val threads: List<SenderThread> = emptyList(),
    val selectedSender: String? = null,
    val selectedCategory: SmsCategory? = null,
    val currentTab: AppTab = AppTab.MESSAGES,
    val searchQuery: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val composing: Boolean = false,
    val contacts: List<SavedContact> = emptyList(),
) {
    fun nameFor(sender: String): String? = contacts.nameFor(sender)
    fun photoFor(sender: String): String? = contacts.photoFor(sender)
    val selectedThread: SenderThread? =
        selectedSender?.let { sender -> threads.firstOrNull { it.sender == sender } }

    val categorySummaries: List<CategorySummary> =
        SmsCategory.entries.map { category ->
            val inCategory = threads.filter { categorizeThread(it) == category }
            CategorySummary(
                category = category,
                count = inCategory.sumOf { it.count },
                lastAtMillis = inCategory.maxOfOrNull { it.lastMessage.receivedAtMillis },
            )
        }

    val threadsInSelectedCategory: List<SenderThread> =
        selectedCategory?.let { category ->
            threads.filter { categorizeThread(it) == category }
        }.orEmpty()

    val searchResults: List<SenderThread>
        get() {
            val needle = searchQuery.trim()
            if (needle.isEmpty()) return threads
            return threads.filter { thread ->
                thread.sender.containsFolded(needle) ||
                    nameFor(thread.sender).orEmpty().containsFolded(needle) ||
                    thread.messages.any { it.body.containsFolded(needle) }
            }
        }
}
