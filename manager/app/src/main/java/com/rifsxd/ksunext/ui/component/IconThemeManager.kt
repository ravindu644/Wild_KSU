package com.rifsxd.ksunext.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.rifsxd.ksunext.R
import com.rifsxd.ksunext.ksuApp
import com.rifsxd.ksunext.ui.util.IconPack
import com.rifsxd.ksunext.ui.util.IconPackHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class IconThemeItem(
    val id: String,
    val name: String,
    val packageName: String?,
    val icon: BitmapPainter?,
    val isEnabled: Boolean,
    val priority: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconThemeManagerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { ksuApp.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    
    var availableIconPacks by remember { mutableStateOf<List<IconPack>>(emptyList()) }
    var iconThemeItems by remember { mutableStateOf<List<IconThemeItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Load available icon packs and current configuration
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val iconPacks = IconPackHelper.getInstalledIconPacks(context)
            availableIconPacks = iconPacks
            
            // Load current theme priorities from preferences
            val currentThemes = prefs.getString("icon_theme_priorities", "") ?: ""
            val enabledThemes = prefs.getStringSet("enabled_icon_themes", setOf()) ?: setOf()
            
            val themeList = currentThemes.split(",").filter { it.isNotEmpty() && it != "default" }
            val items = mutableListOf<IconThemeItem>()
            
            // Add only icon packs (no default option)
            iconPacks.forEachIndexed { index, iconPack ->
                val iconBitmap = iconPack.icon?.toBitmap()
                val iconPainter = iconBitmap?.let { BitmapPainter(it.asImageBitmap()) }
                
                items.add(
                    IconThemeItem(
                        id = iconPack.packageName,
                        name = iconPack.name,
                        packageName = iconPack.packageName,
                        icon = iconPainter,
                        isEnabled = enabledThemes.contains(iconPack.packageName),
                        priority = themeList.indexOf(iconPack.packageName).takeIf { it >= 0 } ?: index
                    )
                )
            }
            
            iconThemeItems = items.sortedBy { it.priority }
            isLoading = false
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.icon_theme),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (!isLoading && iconThemeItems.isNotEmpty()) {
                    Text(
                        text = "Use up/down buttons to reorder priority. Higher items have priority over lower ones.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (iconThemeItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No themes installed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                 text = "Install an icon pack to customize app icons",
                                 style = MaterialTheme.typography.bodyMedium,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant,
                                 textAlign = TextAlign.Center
                             )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(iconThemeItems) { index, item ->
                            IconThemeItemCard(
                                item = item,
                                onToggle = { enabled ->
                                    iconThemeItems = iconThemeItems.map {
                                        if (it.id == item.id) it.copy(isEnabled = enabled) else it
                                    }
                                },
                                onMoveUp = if (index > 0) {
                                    {
                                        val newList = iconThemeItems.toMutableList()
                                        val temp = newList[index]
                                        newList[index] = newList[index - 1]
                                        newList[index - 1] = temp
                                        iconThemeItems = newList
                                    }
                                } else null,
                                onMoveDown = if (index < iconThemeItems.size - 1) {
                                    {
                                        val newList = iconThemeItems.toMutableList()
                                        val temp = newList[index]
                                        newList[index] = newList[index + 1]
                                        newList[index + 1] = temp
                                        iconThemeItems = newList
                                    }
                                } else null
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Save configuration
                    val enabledThemes = iconThemeItems.filter { it.isEnabled }.map { it.id }.toSet()
                    val priorityOrder = iconThemeItems.map { it.id }.joinToString(",")
                    
                    prefs.edit()
                        .putStringSet("enabled_icon_themes", enabledThemes)
                        .putString("icon_theme_priorities", priorityOrder)
                        .putString("icon_theme", enabledThemes.firstOrNull() ?: "default")
                        .apply()
                    
                    onDismiss()
                },
                enabled = iconThemeItems.isNotEmpty()
            ) {
                Text(if (iconThemeItems.isEmpty()) "Close" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun IconThemeItemCard(
    item: IconThemeItem,
    onToggle: (Boolean) -> Unit,
    onMoveUp: (() -> Unit)?,
    onMoveDown: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (item.isEnabled) 2.dp else 1.dp,
                color = if (item.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isEnabled) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (item.icon != null) {
                    Image(
                        painter = item.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Name and package
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = MaterialTheme.typography.titleMedium.lineHeight
                )
                if (item.packageName != null && item.packageName.isNotEmpty()) {
                    Text(
                        text = item.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Controls
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Move up button
                IconButton(
                    onClick = { onMoveUp?.invoke() },
                    enabled = onMoveUp != null
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Move up",
                        tint = if (onMoveUp != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                
                // Move down button
                IconButton(
                    onClick = { onMoveDown?.invoke() },
                    enabled = onMoveDown != null
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Move down",
                        tint = if (onMoveDown != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                
                // Enable/disable switch
                Switch(
                    checked = item.isEnabled,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}