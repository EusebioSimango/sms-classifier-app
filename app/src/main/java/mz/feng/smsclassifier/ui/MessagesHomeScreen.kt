package mz.feng.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.feng.smsclassifier.sms.SenderThread
import mz.feng.smsclassifier.sms.SmsCategory

@Composable
fun MessagesHomeScreen(
    summaries: List<CategorySummary>,
    recentThreads: List<SenderThread>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onOpenCategory: (SmsCategory) -> Unit,
    onOpenThread: (SenderThread) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleRecent = if (searchQuery.isBlank()) {
        recentThreads
    } else {
        val needle = searchQuery.lowercase()
        recentThreads.filter {
            it.sender.lowercase().contains(needle) ||
                it.lastMessage.body.lowercase().contains(needle)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Mensagens",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Pesquisar mensagens") },
            )
        }
        item {
            Text("CATEGORIAS", style = MaterialTheme.typography.labelMedium)
        }
        item {
            CategoryGrid(
                summaries = summaries,
                onOpenCategory = onOpenCategory,
            )
        }
        item {
            Text("MENSAGENS RECENTES", style = MaterialTheme.typography.labelMedium)
        }
        if (visibleRecent.isEmpty()) {
            item {
                Text(
                    "Nenhum SMS na caixa de entrada.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        } else {
            items(visibleRecent, key = { it.sender }) { thread ->
                ThreadRow(thread = thread, onClick = { onOpenThread(thread) })
            }
        }
    }
}
