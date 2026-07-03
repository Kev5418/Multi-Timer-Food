package com.yuquilema.multi_timerfood.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuquilema.multi_timerfood.data.ActiveTimer
import com.yuquilema.multi_timerfood.ui.AppColors
import com.yuquilema.multi_timerfood.ui.components.AppTopBar
import com.yuquilema.multi_timerfood.ui.components.CategoryChip
import com.yuquilema.multi_timerfood.viewmodel.TimerViewModel

@Composable
fun HomeScreen(
    timerViewModel: TimerViewModel,
    onAddTimer: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(AppColors.CreamBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(title = "Multi-Timer Food", icon = Icons.Filled.Timer)

            if (timerViewModel.activeTimers.isEmpty()) {
                EmptyHomeState()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(timerViewModel.activeTimers, key = { it.id }) { timer ->
                        TimerCard(
                            timer = timer,
                            onPauseResume = { timerViewModel.togglePauseResume(timer) },
                            onRestart = { timerViewModel.restartTimer(timer) },
                            onRemove = { timerViewModel.removeTimer(timer, saveAsCompleted = true) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddTimer,
            containerColor = AppColors.OrangePrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Crear temporizador")
        }
    }
}

@Composable
private fun EmptyHomeState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.Timer, contentDescription = null, tint = AppColors.OrangePrimary, modifier = Modifier.size(56.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text("No hay temporizadores activos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Toca el botón + para crear tu primer temporizador", color = Color.Gray, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TimerCard(
    timer: ActiveTimer,
    onPauseResume: () -> Unit,
    onRestart: () -> Unit,
    onRemove: () -> Unit
) {
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
                    Text(timer.foodName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    CategoryChip(timer.category)
                }
                Text(
                    text = timer.formattedTime(),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (timer.isFinished) AppColors.GreenCheck else AppColors.OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { timer.progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = AppColors.OrangePrimary,
                trackColor = AppColors.TrackGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (timer.isFinished) {
                    RoundIconButton(Icons.Filled.Refresh, AppColors.GreenCheck, onRestart)
                    Spacer(modifier = Modifier.width(10.dp))
                    RoundIconButton(Icons.Filled.Add, AppColors.OrangePrimary, onRemove)
                } else {
                    RoundIconButton(
                        icon = if (timer.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        background = AppColors.OrangePrimary,
                        onClick = onPauseResume
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    RoundIconButton(Icons.Filled.Refresh, AppColors.GreenCheck, onRestart)
                }
            }
        }
    }
}

@Composable
private fun RoundIconButton(icon: ImageVector, background: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(40.dp).background(background, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }
}
