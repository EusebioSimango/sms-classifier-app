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

@Composable
fun SearchScreen(
    query: String,
    results: List<SenderThread>,
    onQueryChange: (String) -> Unit,
    onOpenThread: (SenderThread) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Pesquisar", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Texto ou remetente") },
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (query.isBlank()) {
                item {
                    Text(
                        "Escreve para filtrar as conversas já guardadas no dispositivo.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else if (results.isEmpty()) {
                item {
                    Text("Nenhum resultado.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(results, key = { it.sender }) { thread ->
                    ThreadRow(thread = thread, onClick = { onOpenThread(thread) })
                }
            }
        }
    }
}
