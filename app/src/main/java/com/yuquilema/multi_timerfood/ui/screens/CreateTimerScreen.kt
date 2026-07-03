package com.yuquilema.multi_timerfood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuquilema.multi_timerfood.ui.AppColors
import com.yuquilema.multi_timerfood.ui.components.ALL_CATEGORIES
import com.yuquilema.multi_timerfood.ui.components.AppTopBar
import com.yuquilema.multi_timerfood.ui.components.categoryColor
import com.yuquilema.multi_timerfood.viewmodel.TimerViewModel

@Composable
fun CreateTimerScreen(
    timerViewModel: TimerViewModel,
    onBack: () -> Unit,
    onTimerSaved: () -> Unit,
) {
    var foodName by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("0") }
    var minutes by remember { mutableStateOf("0") }
    var seconds by remember { mutableStateOf("0") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.CreamBackground)) {
        AppTopBar(title = "Create Timer", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            OutlinedTextField(
                value = foodName,
                onValueChange = { foodName = it },
                placeholder = { Text("Food name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.OrangePrimary,
                    unfocusedBorderColor = AppColors.BorderColor
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cooking time", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeField("Hours", hours) { hours = it }
                        TimeField("Minutes", minutes) { minutes = it }
                        TimeField("Seconds", seconds) { seconds = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("Category", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            CategoryFlow(selectedCategory) { selectedCategory = it }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Options", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OptionRow(Icons.Filled.NotificationsActive, "Sound notification", soundEnabled) { soundEnabled = it }
                    Spacer(modifier = Modifier.height(10.dp))
                    OptionRow(Icons.Filled.Vibration, "Vibration", vibrationEnabled) { vibrationEnabled = it }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.OrangePrimary)
                ) {
                    Text("Cancel", color = AppColors.OrangePrimary, fontWeight = FontWeight.Bold)
                }

                val totalSeconds = ((hours.toIntOrNull() ?: 0) * 3600) +
                    ((minutes.toIntOrNull() ?: 0) * 60) +
                    (seconds.toIntOrNull() ?: 0)
                val canSave = foodName.isNotBlank() && selectedCategory != null && totalSeconds > 0

                Button(
                    onClick = {
                        timerViewModel.createTimer(
                            foodName = foodName.trim(),
                            category = selectedCategory ?: "",
                            totalSeconds = totalSeconds,
                            soundEnabled = soundEnabled,
                            vibrationEnabled = vibrationEnabled
                        )
                        onTimerSaved()
                    },
                    enabled = canSave,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.OrangePrimary,
                        disabledContainerColor = AppColors.BorderColor
                    )
                ) {
                    Text("Save Timer", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RowScope.TimeField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.weight(1f)) {
        Text(label, fontSize = 12.sp, color = AppColors.TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { new -> if (new.all { it.isDigit() }) onValueChange(new.take(3)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.OrangePrimary,
                unfocusedBorderColor = AppColors.BorderColor
            )
        )
    }
}

@Composable
private fun CategoryFlow(selected: String?, onSelect: (String) -> Unit) {
    val rows = ALL_CATEGORIES.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { category ->
                    val isSelected = category == selected
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) categoryColor(category) else Color.White,
                                shape = RoundedCornerShape(50)
                            )
                            .then(
                                if (!isSelected) Modifier.border(1.dp, AppColors.BorderColor, RoundedCornerShape(50))
                                else Modifier
                            )
                            .selectable(
                                selected = isSelected,
                                onClick = { onSelect(category) },
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(category, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionRow(icon: ImageVector, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = AppColors.OrangePrimary, checkedThumbColor = Color.White)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(icon, contentDescription = null, tint = Color(0xFF3A3A3A), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 14.sp)
    }
}
