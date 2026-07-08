package com.yuquilema.multi_timerfood.ui.components

import com.yuquilema.multi_timerfood.ui.AppColors
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the category helpers backing [CategoryChip]: [ALL_CATEGORIES] and
 * [categoryColor].
 */
class CategoryChipTest {

    @Test
    fun `all categories are listed in order`() {
        assertEquals(
            listOf("Meat", "Pasta", "Eggs", "Vegetables", "Desserts"),
            ALL_CATEGORIES,
        )
    }

    @Test
    fun `known categories map to their palette colors`() {
        assertEquals(AppColors.CategoryMeat, categoryColor("Meat"))
        assertEquals(AppColors.CategoryPasta, categoryColor("Pasta"))
        assertEquals(AppColors.CategoryEggs, categoryColor("Eggs"))
        assertEquals(AppColors.CategoryVegetables, categoryColor("Vegetables"))
        assertEquals(AppColors.CategoryDesserts, categoryColor("Desserts"))
    }

    @Test
    fun `unknown category falls back to default color`() {
        assertEquals(AppColors.CategoryDefault, categoryColor("Fish"))
        assertEquals(AppColors.CategoryDefault, categoryColor(""))
    }

    @Test
    fun `every listed category has a non-default color`() {
        ALL_CATEGORIES.forEach { category ->
            assertEquals(
                "Expected a dedicated color for $category",
                false,
                categoryColor(category) == AppColors.CategoryDefault,
            )
        }
    }
}
