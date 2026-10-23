package mz.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.smsclassifier.sms.SenderThread
import mz.smsclassifier.sms.SmsCategory
import mz.smsclassifier.sms.containsFolded

@Composable
fun CategoryDetailScreen(
    category: SmsCategory,
    threads: List<SenderThread>,
    onBack: () -> Unit,
    onOpenThread: (SenderThread) -> Unit,
    nameOf: (String) -> String? = { null },
    photoOf: (String) -> String? = { null },
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(threads, query, nameOf) {
        val needle = query.trim()
        if (needle.isEmpty()) {
            threads
        } else {
            threads.filter {
                it.sender.containsFolded(needle) ||
                    nameOf(it.sender).orEmpty().containsFolded(needle) ||
                    it.lastMessage.body.containsFolded(needle)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Voltar")
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(category.label, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        text = if (threads.sumOf { it.count } == 1) {
                            "1 SMS"
                        } else {
                            "${threads.sumOf { it.count }} SMS"
                        },
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Pesquisar nesta categoria") },
            )
        }
        if (visible.isEmpty()) {
            item {
                Text("Nenhuma conversa nesta categoria.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            items(visible, key = { it.sender }) { thread ->
                ThreadRow(
                    thread = thread,
                    contactName = nameOf(thread.sender),
                    photoUri = photoOf(thread.sender),
                    onClick = { onOpenThread(thread) },
                )
            }
        }
    }
}
