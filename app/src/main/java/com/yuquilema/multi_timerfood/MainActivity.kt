package com.yuquilema.multi_timerfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yuquilema.multi_timerfood.ui.theme.MultiTimerFoodTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MultiTimerFoodTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    PantallaPrincipal(
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal(modifier: Modifier = Modifier) {

    Text(
        text =
            "¡Bienvenido!\n\n" +
                    "Inicio de sesión exitoso.\n\n" +
                    "Multi-Timer Food\n\n" +
                    "Gestiona múltiples temporizadores para cocinar diferentes alimentos al mismo tiempo.",
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaPrincipal() {
    MultiTimerFoodTheme {
        PantallaPrincipal()
    }
}