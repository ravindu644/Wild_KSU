package com.rifsxd.ksunext.ui.component

import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rifsxd.ksunext.ksuApp
import com.rifsxd.ksunext.ui.util.IconPackHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ThemedIcon(
    packageName: String,
    packageInfo: PackageInfo,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { ksuApp.getSharedPreferences("settings", android.content.Context.MODE_PRIVATE) }
    val selectedIconPack = remember { prefs.getString("icon_theme", "default") ?: "default" }
    
    var themedIcon by remember(packageName, selectedIconPack) { mutableStateOf<BitmapPainter?>(null) }
    var useSystemIcon by remember(packageName, selectedIconPack) { mutableStateOf(selectedIconPack == "default") }
    
    // Load themed icon if an icon pack is selected
    LaunchedEffect(packageName, selectedIconPack) {
        if (selectedIconPack != "default") {
            withContext(Dispatchers.IO) {
                val drawable = IconPackHelper.getThemedIcon(context, selectedIconPack, packageName)
                if (drawable != null) {
                    val bitmap = drawable.toBitmap()
                    themedIcon = BitmapPainter(bitmap.asImageBitmap())
                    useSystemIcon = false
                } else {
                    useSystemIcon = true
                }
            }
        } else {
            useSystemIcon = true
            themedIcon = null
        }
    }
    
    if (useSystemIcon || themedIcon == null) {
        // Use system default icon
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(packageInfo)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        // Use themed icon
        themedIcon?.let { painter ->
            Image(
                painter = painter,
                contentDescription = contentDescription,
                modifier = modifier
            )
        }
    }
}