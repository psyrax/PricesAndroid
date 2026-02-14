package com.psyrax.pokeprices

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.psyrax.pokeprices.ui.navigation.MainNavHost
import com.psyrax.pokeprices.ui.theme.PokePricesTheme

class MainActivity : ComponentActivity() {

    private var deepLinkCardId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)

        setContent {
            PokePricesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavHost(deepLinkCardId = deepLinkCardId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null && uri.scheme == "ogl" && uri.host == "card") {
            val cardId = uri.getQueryParameter("id")
            if (cardId != null) {
                deepLinkCardId = cardId
            }
        }
    }
}
