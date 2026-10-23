package mz.smsclassifier.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import mz.smsclassifier.sms.IncomingSmsNotifier
import mz.smsclassifier.sms.SmsPermissions

@Composable
fun InboxRoute(
    openSender: String? = null,
    onOpenSenderConsumed: () -> Unit = {},
    viewModel: InboxViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        viewModel.refreshPermission()
    }
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { }

    LaunchedEffect(Unit) {
        viewModel.refreshPermission()
    }

    LaunchedEffect(openSender) {
        val sender = openSender ?: return@LaunchedEffect
        viewModel.openThread(sender)
        onOpenSenderConsumed()
    }

    LaunchedEffect(uiState.hasPermission) {
        if (!uiState.hasPermission || Build.VERSION.SDK_INT < 33) return@LaunchedEffect
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted || !IncomingSmsNotifier.shouldAskPermission(context)) return@LaunchedEffect
        IncomingSmsNotifier.markPermissionAsked(context)
        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    BackHandler(
        enabled = uiState.composing || uiState.selectedSender != null || uiState.selectedCategory != null,
    ) {
        when {
            uiState.composing -> viewModel.closeComposer()
            uiState.selectedSender != null -> viewModel.closeThread()
            else -> viewModel.closeCategory()
        }
    }

    val showBottomBar = uiState.hasPermission && uiState.selectedSender == null && !uiState.composing
    val showNewMessage = showBottomBar && uiState.currentTab == AppTab.MESSAGES &&
        uiState.selectedCategory == null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (showNewMessage) {
                FloatingActionButton(onClick = viewModel::openComposer) {
                    Icon(Icons.Filled.Add, contentDescription = "Nova mensagem")
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.MESSAGES,
                        onClick = { viewModel.selectTab(AppTab.MESSAGES) },
                        icon = { Icon(Icons.Outlined.Sms, contentDescription = "SMS") },
                        label = { Text("SMS") },
                    )
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.CATEGORIES,
                        onClick = { viewModel.selectTab(AppTab.CATEGORIES) },
                        icon = { Icon(Icons.Outlined.Category, contentDescription = "Categorias") },
                        label = { Text("Categorias") },
                    )
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.SEARCH,
                        onClick = { viewModel.selectTab(AppTab.SEARCH) },
                        icon = { Icon(Icons.Outlined.Search, contentDescription = "Pesquisar") },
                        label = { Text("Pesquisar") },
                    )
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.SETTINGS,
                        onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Definições") },
                        label = { Text("Definições") },
                    )
                }
            }
        },
    ) { innerPadding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        val selectedThread = uiState.selectedThread
        val selectedCategory = uiState.selectedCategory
        when {
            uiState.composing -> NewMessageScreen(
                contacts = uiState.contacts,
                sending = viewModel.replySending.collectAsStateWithLifecycle().value,
                sendFailed = viewModel.replyFailed.collectAsStateWithLifecycle().value,
                onBack = viewModel::closeComposer,
                onContactsPermissionGranted = viewModel::refreshContacts,
                simLines = viewModel.simLines.collectAsStateWithLifecycle().value,
                selectedSimId = viewModel.selectedSimId.collectAsStateWithLifecycle().value,
                onCycleSim = viewModel::cycleSim,
                onSimsReady = viewModel::refreshSims,
                onSend = viewModel::startConversation,
                modifier = contentModifier,
            )
            !uiState.hasPermission -> PermissionGateScreen(
                onRequestPermissions = { launcher.launch(SmsPermissions.required) },
                modifier = contentModifier,
            )
            selectedThread != null -> {
                val sending by viewModel.replySending.collectAsStateWithLifecycle()
                val sendFailed by viewModel.replyFailed.collectAsStateWithLifecycle()
                SenderThreadScreen(
                    thread = selectedThread,
                    title = uiState.nameFor(selectedThread.sender) ?: selectedThread.sender,
                    photoUri = uiState.photoFor(selectedThread.sender),
                    sending = sending,
                    sendFailed = sendFailed,
                    onBack = viewModel::closeThread,
                    onSend = viewModel::sendReply,
                    simLines = viewModel.simLines.collectAsStateWithLifecycle().value,
                    selectedSimId = viewModel.selectedSimId.collectAsStateWithLifecycle().value,
                    onCycleSim = viewModel::cycleSim,
                    onSimsReady = viewModel::refreshSims,
                    modifier = contentModifier,
                )
            }
            selectedCategory != null -> CategoryDetailScreen(
                category = selectedCategory,
                threads = uiState.threadsInSelectedCategory,
                onBack = viewModel::closeCategory,
                onOpenThread = { viewModel.openThread(it.sender) },
                nameOf = uiState::nameFor,
                photoOf = uiState::photoFor,
                modifier = contentModifier,
            )
            uiState.currentTab == AppTab.CATEGORIES -> CategoriesScreen(
                summaries = uiState.categorySummaries,
                onOpenCategory = viewModel::openCategory,
                modifier = contentModifier,
            )
            uiState.currentTab == AppTab.SEARCH -> SearchScreen(
                query = uiState.searchQuery,
                results = uiState.searchResults,
                onQueryChange = viewModel::setSearchQuery,
                onOpenThread = { viewModel.openThread(it.sender) },
                nameOf = uiState::nameFor,
                photoOf = uiState::photoFor,
                modifier = contentModifier,
            )
            uiState.currentTab == AppTab.SETTINGS -> SettingsScreen(
                themeMode = uiState.themeMode,
                onThemeModeChange = viewModel::setThemeMode,
                modifier = contentModifier,
            )
            else -> MessagesHomeScreen(
                summaries = uiState.categorySummaries,
                recentThreads = uiState.threads,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = viewModel::setSearchQuery,
                onOpenCategory = viewModel::openCategory,
                onOpenThread = { viewModel.openThread(it.sender) },
                nameOf = uiState::nameFor,
                photoOf = uiState::photoFor,
                modifier = contentModifier,
            )
        }
    }
}
