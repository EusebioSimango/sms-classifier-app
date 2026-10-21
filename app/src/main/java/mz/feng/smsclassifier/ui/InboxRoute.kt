package mz.feng.smsclassifier.ui

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

    BackHandler(enabled = uiState.selectedSender != null || uiState.selectedCategory != null) {
        if (uiState.selectedSender != null) {
            viewModel.closeThread()
        } else {
            viewModel.closeCategory()
        }
    }

    val showBottomBar = uiState.hasPermission && uiState.selectedSender == null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
            !uiState.hasPermission -> PermissionGateScreen(
                onRequestPermissions = { launcher.launch(SmsPermissions.required) },
                modifier = contentModifier,
            )
            selectedThread != null -> SenderThreadScreen(
                thread = selectedThread,
                onBack = viewModel::closeThread,
                modifier = contentModifier,
            )
            selectedCategory != null -> CategoryDetailScreen(
                category = selectedCategory,
                threads = uiState.threadsInSelectedCategory,
                onBack = viewModel::closeCategory,
                onOpenThread = { viewModel.openThread(it.sender) },
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
                modifier = contentModifier,
            )
        }
    }
}
