package com.yuquilema.multi_timerfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.yuquilema.multi_timerfood.navigation.MainNavGraph
import com.yuquilema.multi_timerfood.ui.theme.MultiTimerFoodTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MultiTimerFoodTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { _ ->
                    // El padding interno no se usa: cada pantalla maneja su propio
                    // TopBar/BottomBar para calzar exactamente con el diseño de las capturas.
                    MainNavGraph()
                }
            }
        }
    }
}
