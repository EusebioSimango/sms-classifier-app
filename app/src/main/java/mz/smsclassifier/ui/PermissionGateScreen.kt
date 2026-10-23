package mz.smsclassifier.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val disclosurePoints = listOf(
    "O que é lido" to "O texto, o remetente e a hora dos SMS que recebes, e até 100 mensagens já existentes na caixa de entrada.",
    "Para quê" to "Apenas para classificar cada mensagem por categoria (por exemplo carteira móvel, operadora ou suspeita) e para a pesquisa.",
    "Onde fica" to "A classificação fica só neste telemóvel, numa base de dados cifrada. A aplicação não tem internet e não partilha os SMS com ninguém.",
    "Respostas" to "Se responderes a um número de telefone, ou escreveres uma mensagem nova, essa mensagem sai por SMS, pelo operador, só para esse número. Códigos de operadora e bancos não têm campo de resposta.",
    "Contactos" to "Se autorizares, o nome e a fotografia do contacto aparecem na conversa em vez de só o número. A lista de contactos não sai do aparelho. Podes usar a aplicação sem esta permissão.",
    "Notificações" to "Se autorizares, um aviso aparece quando chega um SMS. No ecrã de bloqueio só diz que há uma mensagem nova. Desbloqueado, mostra o nome ou o número e a categoria. O texto não entra no aviso. Podes recusar e continuar a usar a aplicação.",
    "O teu controlo" to "Podes retirar a permissão a qualquer momento nas definições do Android. Ao desinstalar a aplicação, os dados são apagados.",
)

@Composable
fun PermissionGateScreen(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as? Activity
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Acesso aos teus SMS",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "O Categorizador SMS precisa de ler e receber SMS para funcionar. Antes de pedir a permissão, explicamos como os dados são usados.",
            style = MaterialTheme.typography.bodyMedium,
        )
        disclosurePoints.forEach { (title, body) ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(text = body, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Button(onClick = onRequestPermissions, modifier = Modifier.fillMaxWidth()) {
            Text("Aceitar e continuar")
        }
        OutlinedButton(onClick = { activity?.finish() }, modifier = Modifier.fillMaxWidth()) {
            Text("Agora não")
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Se o pedido de SMS não aparecer ou o interruptor estiver cinzento: Definições → Aplicações → Categorizador SMS → menu ⋮ → Permitir definições restritas. Depois volta aqui e toca em «Aceitar e continuar».",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
