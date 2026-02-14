package com.psyrax.pokeprices.ui.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object PriceFormatter {
    fun formatUSD(price: Double?): String {
        if (price == null) return "Sin precio"
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        return formatter.format(price)
    }

    fun formatMXN(priceUSD: Double?, rate: Double): String {
        if (priceUSD == null) return "Sin precio"
        val priceMXN = priceUSD * rate
        val formatter = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
        return formatter.format(priceMXN)
    }

    fun formatDate(timestamp: Int): String {
        val date = Date(timestamp.toLong() * 1000)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
        return formatter.format(date)
    }
}
