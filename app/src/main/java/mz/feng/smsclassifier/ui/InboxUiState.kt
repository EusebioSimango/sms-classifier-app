package mz.feng.smsclassifier.ui

import mz.feng.smsclassifier.settings.ThemeMode
import mz.feng.smsclassifier.sms.SenderThread
import mz.feng.smsclassifier.sms.SmsCategory
import mz.feng.smsclassifier.sms.categorizeThread

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
) {
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
            val needle = searchQuery.trim().lowercase()
            if (needle.isEmpty()) return threads
            return threads.filter { thread ->
                thread.sender.lowercase().contains(needle) ||
                    thread.messages.any { it.body.lowercase().contains(needle) }
            }
        }
}
