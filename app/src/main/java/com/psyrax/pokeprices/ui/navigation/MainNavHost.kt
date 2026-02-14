package com.psyrax.pokeprices.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.psyrax.pokeprices.data.model.CartaListType
import com.psyrax.pokeprices.data.model.CartaWithVariants
import com.psyrax.pokeprices.ui.screens.*
import com.psyrax.pokeprices.ui.viewmodel.*

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.ForSale, "En Venta", Icons.Default.Sell),
    BottomNavItem(Screen.WantToBuy, "Comprar", Icons.Default.ShoppingCart),
    BottomNavItem(Screen.Search, "Buscar", Icons.Default.Search),
    BottomNavItem(Screen.Settings, "Config", Icons.Default.Settings)
)

@Composable
fun MainNavHost(
    deepLinkCardId: String? = null,
    navController: NavHostController = rememberNavController()
) {
    val cartaListViewModel: CartaListViewModel = viewModel()
    val searchViewModel: SearchViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    // Collect shared state
    val usdToMxnRate by cartaListViewModel.usdToMxnRate.collectAsStateWithLifecycle(initialValue = 18.5)
    val apiKey by settingsViewModel.apiKey.collectAsStateWithLifecycle(initialValue = "")

    // Handle deep link
    val deepLinkCarta by cartaListViewModel.deepLinkCarta.collectAsStateWithLifecycle()
    var showDeepLinkDetail by remember { mutableStateOf<CartaWithVariants?>(null) }

    LaunchedEffect(deepLinkCardId) {
        deepLinkCardId?.let { cartaListViewModel.handleDeepLink(it) }
    }

    LaunchedEffect(deepLinkCarta) {
        deepLinkCarta?.let {
            showDeepLinkDetail = it
            cartaListViewModel.clearDeepLink()
        }
    }

    // Search detail sheet state
    var searchDetailCarta by remember { mutableStateOf<CartaWithVariants?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ForSale.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // For Sale tab
            composable(Screen.ForSale.route) {
                val cartas by cartaListViewModel.forSaleCartas.collectAsStateWithLifecycle(initialValue = emptyList())
                ForSaleScreen(
                    cartas = cartas,
                    usdToMxnRate = usdToMxnRate,
                    onAddCarta = {
                        cartaListViewModel.addNewCarta(CartaListType.FOR_SALE)
                    },
                    onEditCarta = { id -> navController.navigate(Screen.CartaEdit.createRoute(id)) },
                    onViewDetail = { id -> navController.navigate(Screen.CartaDetail.createRoute(id)) },
                    onDeleteCarta = { cartaListViewModel.deleteCarta(it.carta) }
                )
            }

            // Want to Buy tab
            composable(Screen.WantToBuy.route) {
                val cartas by cartaListViewModel.wantToBuyCartas.collectAsStateWithLifecycle(initialValue = emptyList())
                WantToBuyScreen(
                    cartas = cartas,
                    usdToMxnRate = usdToMxnRate,
                    onAddCarta = {
                        cartaListViewModel.addNewCarta(CartaListType.WANT_TO_BUY)
                    },
                    onEditCarta = { id -> navController.navigate(Screen.CartaEdit.createRoute(id)) },
                    onViewDetail = { id -> navController.navigate(Screen.CartaDetail.createRoute(id)) },
                    onDeleteCarta = { cartaListViewModel.deleteCarta(it.carta) }
                )
            }

            // Search tab
            composable(Screen.Search.route) {
                val searchText by searchViewModel.searchText.collectAsStateWithLifecycle()
                val selectedSet by searchViewModel.selectedSet.collectAsStateWithLifecycle()
                val sets by searchViewModel.sets.collectAsStateWithLifecycle(initialValue = emptyList())
                val isSearching by searchViewModel.isSearching.collectAsStateWithLifecycle()
                val results by searchViewModel.results.collectAsStateWithLifecycle()
                val errorMessage by searchViewModel.errorMessage.collectAsStateWithLifecycle()
                val hasSearched by searchViewModel.hasSearched.collectAsStateWithLifecycle()

                SearchScreen(
                    searchText = searchText,
                    selectedSet = selectedSet,
                    sets = sets,
                    isSearching = isSearching,
                    results = results,
                    errorMessage = errorMessage,
                    hasSearched = hasSearched,
                    apiKey = apiKey,
                    usdToMxnRate = usdToMxnRate,
                    onSearchTextChange = searchViewModel::updateSearchText,
                    onSetSelected = searchViewModel::selectSet,
                    onSearch = { searchViewModel.performSearch(apiKey) },
                    onCardSelected = { searchDetailCarta = it }
                )
            }

            // Settings tab
            composable(Screen.Settings.route) {
                val currentRate by settingsViewModel.usdToMxnRate.collectAsStateWithLifecycle(initialValue = 18.5)
                val isRefreshingSets by settingsViewModel.isRefreshingSets.collectAsStateWithLifecycle()
                val refreshMessage by settingsViewModel.refreshMessage.collectAsStateWithLifecycle()
                val isUpdatingCards by settingsViewModel.isUpdatingCards.collectAsStateWithLifecycle()
                val updateProgress by settingsViewModel.updateProgress.collectAsStateWithLifecycle()
                val isUpdatingRate by settingsViewModel.isUpdatingRate.collectAsStateWithLifecycle()
                val rateUpdateMessage by settingsViewModel.rateUpdateMessage.collectAsStateWithLifecycle()
                val cartaCount by settingsViewModel.cartaCount.collectAsStateWithLifecycle()

                SettingsScreen(
                    apiKey = apiKey,
                    usdToMxnRate = currentRate,
                    isRefreshingSets = isRefreshingSets,
                    refreshMessage = refreshMessage,
                    isUpdatingCards = isUpdatingCards,
                    updateProgress = updateProgress,
                    isUpdatingRate = isUpdatingRate,
                    rateUpdateMessage = rateUpdateMessage,
                    cartaCount = cartaCount,
                    onApiKeyChange = settingsViewModel::saveApiKey,
                    onRateChange = { text ->
                        text.toDoubleOrNull()?.let { settingsViewModel.saveRate(it) }
                    },
                    onRefreshSets = { settingsViewModel.refreshSets(apiKey) },
                    onUpdateAllCards = { settingsViewModel.updateAllCards(apiKey) },
                    onUpdateExchangeRate = settingsViewModel::updateExchangeRate
                )
            }

            // Carta Detail
            composable(
                route = Screen.CartaDetail.route,
                arguments = listOf(navArgument("cartaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cartaId = backStackEntry.arguments?.getString("cartaId") ?: return@composable
                var cartaWithVariants by remember { mutableStateOf<CartaWithVariants?>(null) }

                LaunchedEffect(cartaId) {
                    cartaWithVariants = cartaListViewModel.getCartaById(cartaId)
                }

                cartaWithVariants?.let {
                    CartaDetailScreen(
                        cartaWithVariants = it,
                        usdToMxnRate = usdToMxnRate,
                        onDismiss = { navController.popBackStack() }
                    )
                }
            }

            // Carta Edit
            composable(
                route = Screen.CartaEdit.route,
                arguments = listOf(navArgument("cartaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cartaId = backStackEntry.arguments?.getString("cartaId") ?: return@composable
                val editViewModel: CartaEditViewModel = viewModel()

                LaunchedEffect(cartaId) { editViewModel.loadCarta(cartaId) }

                val carta by editViewModel.carta.collectAsStateWithLifecycle()
                val sets by editViewModel.sets.collectAsStateWithLifecycle(initialValue = emptyList())
                val isFetching by editViewModel.isFetching.collectAsStateWithLifecycle()
                val fetchMessage by editViewModel.fetchMessage.collectAsStateWithLifecycle()
                val foundCards by editViewModel.foundCards.collectAsStateWithLifecycle()

                CartaEditScreen(
                    cartaWithVariants = carta,
                    sets = sets,
                    isFetching = isFetching,
                    fetchMessage = fetchMessage,
                    foundCards = foundCards,
                    onUpdateCarta = { name, expCode, expName, cardNum, priceText, currency, tagId, imageURL ->
                        carta?.let { current ->
                            val updated = current.carta.copy(
                                name = name,
                                expansionCode = expCode,
                                expansionName = expName,
                                cardNumber = cardNum,
                                price = priceText.toDoubleOrNull(),
                                currency = currency,
                                tagId = tagId.ifBlank { null },
                                imageURL = imageURL.ifBlank { null }
                            )
                            editViewModel.updateCarta(updated, current.variants)
                        }
                    },
                    onFetchCardInfo = { name, setId ->
                        editViewModel.fetchCardInfo(name, setId, apiKey)
                    },
                    onSelectCard = editViewModel::applyCardData,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Detail sheet from search or deep link
        val detailCarta = searchDetailCarta ?: showDeepLinkDetail
        if (detailCarta != null) {
            CartaDetailScreen(
                cartaWithVariants = detailCarta,
                usdToMxnRate = usdToMxnRate,
                onDismiss = {
                    searchDetailCarta = null
                    showDeepLinkDetail = null
                }
            )
        }
    }
}
