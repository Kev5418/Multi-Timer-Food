package com.yuquilema.multi_timerfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yuquilema.multi_timerfood.ui.AppColors

val ALL_CATEGORIES = listOf("Meat", "Pasta", "Eggs", "Vegetables", "Desserts")

fun categoryColor(category: String): Color = when (category) {
    "Meat" -> AppColors.CategoryMeat
    "Pasta" -> AppColors.CategoryPasta
    "Eggs" -> AppColors.CategoryEggs
    "Vegetables" -> AppColors.CategoryVegetables
    "Desserts" -> AppColors.CategoryDesserts
    else -> AppColors.CategoryDefault
}

@Composable
fun CategoryChip(category: String, modifier: Modifier = Modifier) {
    Text(
        text = category,
        modifier = modifier
            .background(categoryColor(category), shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        color = Color(0xFF3A3A3A),
        style = MaterialTheme.typography.labelSmall
    )
}
