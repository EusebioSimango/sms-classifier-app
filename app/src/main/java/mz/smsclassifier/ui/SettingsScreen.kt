package mz.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mz.smsclassifier.settings.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    messagesFirst: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onMessagesFirstChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(
        ThemeMode.LIGHT to "Claro",
        ThemeMode.DARK to "Escuro",
        ThemeMode.SYSTEM to "Sistema",
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Definições", style = MaterialTheme.typography.headlineMedium)
        Text("Tema", style = MaterialTheme.typography.titleMedium)
        Text(
            "O processamento continua local. A escolha do tema fica neste dispositivo.",
            style = MaterialTheme.typography.bodyMedium,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, (mode, label) ->
                SegmentedButton(
                    selected = themeMode == mode,
                    onClick = { onThemeModeChange(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index, options.size),
                ) {
                    Text(label)
                }
            }
        }
        Text("Na aba SMS", style = MaterialTheme.typography.titleMedium)
        Text(
            "As categorias continuam na aba delas. Aqui escolhes o que aparece primeiro na aba SMS.",
            style = MaterialTheme.typography.bodyMedium,
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val homeOptions = listOf(true to "Mensagens", false to "Categorias")
            homeOptions.forEachIndexed { index, (first, label) ->
                SegmentedButton(
                    selected = messagesFirst == first,
                    onClick = { onMessagesFirstChange(first) },
                    shape = SegmentedButtonDefaults.itemShape(index, homeOptions.size),
                ) {
                    Text(label)
                }
            }
        }
    }
}
