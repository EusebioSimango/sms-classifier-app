package mz.feng.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.feng.smsclassifier.sms.SenderThread
import mz.feng.smsclassifier.sms.asPreview

@Composable
fun SenderListScreen(
    threads: List<SenderThread>,
    onOpenThread: (SenderThread) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Categorizador SMS",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = if (threads.isEmpty()) {
                "Nenhum SMS na caixa de entrada."
            } else {
                "${threads.size} conversas"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(threads, key = { it.sender }) { thread ->
                Card(
                    onClick = { onOpenThread(thread) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = thread.sender.ifBlank { "(sem remetente)" },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = formatTimestamp(thread.lastMessage.receivedAtMillis),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = thread.lastMessage.body.asPreview(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = if (thread.count == 1) "1 SMS" else "${thread.count} SMS",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
    }
}
