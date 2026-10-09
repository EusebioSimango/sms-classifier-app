package mz.feng.smsclassifier.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionGateScreen(
    onRequestPermissions: () -> Unit,
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
            text = "Esta aplicação precisa de RECEIVE_SMS e READ_SMS para captar mensagens localmente, sem as enviar para a rede.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onRequestPermissions) {
            Text("Conceder permissões de SMS")
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Se o pedido de SMS não aparecer ou o interruptor estiver cinzento: Definições → Aplicações → Categorizador SMS → menu ⋮ → Permitir definições restritas. Depois volta aqui e toca no botão acima.\n\nO Play Protect pode bloquear a instalação deste APK se o copiares por WhatsApp ou gestor de ficheiros. Instala por USB com ./install-debug.sh.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
