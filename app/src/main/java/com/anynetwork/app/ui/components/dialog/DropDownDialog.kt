package com.anynetwork.app.ui.components.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun ExpandableDrillDownMenu(
    menuData: List<DropDownDialogMenuCategory>,
    onDismiss: () -> Unit
) {
    // State to track which categories are expanded
    var expandedCategory by remember { mutableStateOf<DropDownDialogMenuCategory?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        // Centered Box for the dialog content
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable { onDismiss.invoke() },
            contentAlignment = Alignment.Center,
        ) {
            // Card to contain the menu
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.6f),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Display the current menu items in a LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Iterate through the main categories
                        items(menuData) { category ->
                            MenuCategoryItem(
                                category = category,
                                isExpanded = category == expandedCategory,
                                onExpandClick = {
                                    expandedCategory = if (expandedCategory == category) null else category
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCategoryItem(
    category: DropDownDialogMenuCategory,
    isExpanded: Boolean,
    onExpandClick: () -> Unit
) {
    var expandedSubCategory by remember { mutableStateOf<DropDownDialogMenuCategory?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Main category button
        TextButton(onClick = onExpandClick, modifier = Modifier.fillMaxWidth()) {
            if (category.subCategories.isNotEmpty()) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Chevron icon (ExpandMore if collapsed, ExpandLess if expanded)
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    // Category name
                    Text(category.name, color = Color.White)
                }
            } else {
                TextButton(
                    onClick = { category.onClick?.invoke() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(category.name, color = Color.White)
                }
            }
        }

        // Subcategories are shown if the category is expanded
        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                category.subCategories.forEach { subCategory ->
                    // If the subCategory also has its own subcategories, make it expandable
                    if (subCategory.subCategories.isNotEmpty()) {
                        // Recursive expandable subcategories
                        MenuCategoryItem(
                            category = subCategory,
                            isExpanded = subCategory == expandedSubCategory,
                            onExpandClick = {
                                expandedSubCategory = if (expandedSubCategory == subCategory) null else subCategory
                            }
                        )
                    } else {
                        // Non-expandable subcategory (leaf item)
                        TextButton(
                            onClick = { subCategory.onClick?.invoke() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(subCategory.name, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Sample Data Structure for Menu
data class DropDownDialogMenuCategory(
    val name: String,
    val subCategories: List<DropDownDialogMenuCategory> = emptyList(),
    val onClick: (() -> Unit)? = null
)