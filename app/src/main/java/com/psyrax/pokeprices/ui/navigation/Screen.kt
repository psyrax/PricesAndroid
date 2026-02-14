package com.psyrax.pokeprices.ui.navigation

sealed class Screen(val route: String) {
    data object ForSale : Screen("for_sale")
    data object WantToBuy : Screen("want_to_buy")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object CartaDetail : Screen("carta_detail/{cartaId}") {
        fun createRoute(cartaId: String) = "carta_detail/$cartaId"
    }
    data object CartaEdit : Screen("carta_edit/{cartaId}") {
        fun createRoute(cartaId: String) = "carta_edit/$cartaId"
    }
}
