package mz.feng.smsclassifier.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mz.feng.smsclassifier.sms.SmsPermissions

@Composable
fun InboxRoute(
    viewModel: InboxViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        viewModel.refreshPermission()
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermission()
    }

    BackHandler(enabled = uiState.selectedSender != null) {
        viewModel.closeThread()
    }

    Scaffold { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        val selectedThread = uiState.selectedThread
        when {
            !uiState.hasPermission -> PermissionGateScreen(
                onRequestPermissions = { launcher.launch(SmsPermissions.required) },
                modifier = contentModifier,
            )
            selectedThread != null -> SenderThreadScreen(
                thread = selectedThread,
                onBack = viewModel::closeThread,
                modifier = contentModifier,
            )
            else -> SenderListScreen(
                threads = uiState.threads,
                onOpenThread = { viewModel.openThread(it.sender) },
                modifier = contentModifier,
            )
        }
    }
}
