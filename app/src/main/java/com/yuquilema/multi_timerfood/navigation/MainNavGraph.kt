package com.yuquilema.multi_timerfood.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuquilema.multi_timerfood.ui.components.AppScreen
import com.yuquilema.multi_timerfood.ui.components.BottomNavBar
import com.yuquilema.multi_timerfood.ui.screens.CreateTimerScreen
import com.yuquilema.multi_timerfood.ui.screens.HistoryScreen
import com.yuquilema.multi_timerfood.ui.screens.HomeScreen
import com.yuquilema.multi_timerfood.viewmodel.TimerViewModel

/**
 * Contenedor de las 3 pantallas principales (después del login/registro,
 * que siguen siendo LoginActivity/RegisterActivity con Views + XML).
 */
@Composable
fun MainNavGraph() {
    val timerViewModel: TimerViewModel = viewModel()
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    timerViewModel = timerViewModel,
                    onAddTimer = { currentScreen = AppScreen.CREATE_TIMER }
                )
                AppScreen.CREATE_TIMER -> CreateTimerScreen(
                    timerViewModel = timerViewModel,
                    onBack = { currentScreen = AppScreen.HOME },
                    onTimerSaved = { currentScreen = AppScreen.HOME }
                )
                AppScreen.HISTORY -> HistoryScreen(timerViewModel = timerViewModel)
            }
        }
        BottomNavBar(current = currentScreen, onNavigate = { currentScreen = it })
    }
}
