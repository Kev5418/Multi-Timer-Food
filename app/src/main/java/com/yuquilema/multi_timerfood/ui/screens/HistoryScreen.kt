package com.yuquilema.multi_timerfood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuquilema.multi_timerfood.data.TimerHistoryItem
import com.yuquilema.multi_timerfood.ui.AppColors
import com.yuquilema.multi_timerfood.ui.components.ALL_CATEGORIES
import com.yuquilema.multi_timerfood.ui.components.AppTopBar
import com.yuquilema.multi_timerfood.ui.components.CategoryChip
import com.yuquilema.multi_timerfood.viewmodel.TimerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(timerViewModel: TimerViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }

    val filtered = timerViewModel.history.filter { item ->
        (searchQuery.isBlank() || item.foodName.contains(searchQuery, ignoreCase = true)) &&
            (categoryFilter == null || item.category == categoryFilter)
    }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.CreamBackground)) {
        AppTopBar(
            title = "History",
            icon = Icons.Filled.History,
            trailing = {
                Icon(
                    Icons.Filled.DeleteSweep,
                    contentDescription = "Borrar historial",
                    tint = Color.White,
                    modifier = Modifier.clickable { timerViewModel.clearHistory() }
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.OrangePrimary,
                    unfocusedBorderColor = AppColors.BorderColor
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                IconButton(onClick = { showFilterMenu = true }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filtrar", tint = AppColors.OrangePrimary)
                }
                DropdownMenu(expanded = showFilterMenu, onDismissRequest = { showFilterMenu = false }) {
                    DropdownMenuItem(text = { Text("Todas las categorías") }, onClick = { categoryFilter = null; showFilterMenu = false })
                    ALL_CATEGORIES.forEach { cat ->
                        DropdownMenuItem(text = { Text(cat) }, onClick = { categoryFilter = cat; showFilterMenu = false })
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.History, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sin historial todavía", color = AppColors.TextSecondary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    HistoryCard(item = item, onDelete = { timerViewModel.deleteHistoryItem(item) })
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: TimerHistoryItem, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault()) }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(item.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    CategoryChip(item.category)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = if (item.isCompleted) "Completado" else "Incompleto",
                        tint = if (item.isCompleted) AppColors.GreenCheck else AppColors.TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(formatDuration(item.totalSeconds), color = AppColors.OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(dateFormat.format(Date(item.dateMillis)), fontSize = 12.sp, color = AppColors.TextSecondary)
                }
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = AppColors.RedDelete,
                    modifier = Modifier.clickable(onClick = onDelete)
                )
            }
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", m, s)
}
