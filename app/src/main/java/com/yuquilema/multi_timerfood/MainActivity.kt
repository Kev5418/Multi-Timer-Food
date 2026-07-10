package com.yuquilema.multi_timerfood

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.yuquilema.multi_timerfood.navigation.MainNavGraph
import com.yuquilema.multi_timerfood.notificaciones.NotificationChannels
import com.yuquilema.multi_timerfood.ui.theme.MultiTimerFoodTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Si lo niega, la app sigue funcionando; solo no llegan avisos del sistema. */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationChannels.createChannels(this)
        requestNotificationPermissionIfNeeded()

        enableEdgeToEdge()

        setContent {
            MultiTimerFoodTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // El padding interno no se usa directamente en MainNavGraph pero se pasa para evitar advertencias
                    MainNavGraph()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < 33) return // No existe este permiso antes de Android 13
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}