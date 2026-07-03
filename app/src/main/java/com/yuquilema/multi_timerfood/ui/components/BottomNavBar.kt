package com.yuquilema.multi_timerfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.yuquilema.multi_timerfood.ui.AppColors

enum class AppScreen { HOME, CREATE_TIMER, HISTORY }

@Composable
fun BottomNavBar(current: AppScreen, onNavigate: (AppScreen) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem("Home", Icons.Filled.Home, current == AppScreen.HOME) { onNavigate(AppScreen.HOME) }
        NavItem("Create Timer", Icons.Filled.AddCircle, current == AppScreen.CREATE_TIMER) { onNavigate(AppScreen.CREATE_TIMER) }
        NavItem("History", Icons.Filled.History, current == AppScreen.HISTORY) { onNavigate(AppScreen.HISTORY) }
    }
}

@Composable
private fun RowScope.NavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val tint = if (selected) AppColors.OrangePrimary else AppColors.TextSecondary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint)
        Text(text = label, color = tint, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun AppTopBar(
    title: String,
    icon: ImageVector? = null,
    trailing: (@Composable () -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.OrangePrimary)
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when {
                onBack != null -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.clickable(onClick = onBack)
                    )
                }
                icon != null -> {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.padding(start = 8.dp))
            Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
        trailing?.invoke()
    }
}
