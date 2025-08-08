package com.rifsxd.ksunext.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat

data class IconPack(
    val packageName: String,
    val name: String,
    val icon: Drawable?
)

object IconPackHelper {
    private const val TAG = "IconPackHelper"
    
    // Common icon pack intents and categories
    private val ICON_PACK_INTENTS = listOf(
        "org.adw.launcher.THEMES",
        "com.gau.go.launcherex.theme",
        "com.novalauncher.THEME",
        "com.teslacoilsw.launcher.THEME",
        "com.fede.launcher.THEME_ICONPACK",
        "com.anddoes.launcher.THEME",
        "com.actionlauncher.playstore.THEME",
        "ch.deletescape.lawnchair.THEME",
        "app.lawnchair.THEME"
    )
    
    private val ICON_PACK_CATEGORIES = listOf(
        "com.anddoes.launcher.THEME",
        "com.fede.launcher.THEME_ICONPACK",
        "com.novalauncher.THEME",
        "com.teslacoilsw.launcher.THEME",
        "com.gau.go.launcherex.theme"
    )
    
    /**
     * Get all installed icon packs on the system
     */
    fun getInstalledIconPacks(context: Context): List<IconPack> {
        val iconPacks = mutableSetOf<IconPack>()
        val packageManager = context.packageManager
        
        // Search by intents
        for (intentAction in ICON_PACK_INTENTS) {
            try {
                val intent = Intent(intentAction)
                val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                
                for (resolveInfo in resolveInfos) {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appInfo = try {
                        packageManager.getApplicationInfo(packageName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        continue
                    }
                    
                    val name = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = try {
                        packageManager.getApplicationIcon(packageName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                    
                    iconPacks.add(IconPack(packageName, name, icon))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error querying intent $intentAction", e)
            }
        }
        
        // Search by categories
        for (category in ICON_PACK_CATEGORIES) {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(category)
                }
                val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
                
                for (resolveInfo in resolveInfos) {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appInfo = try {
                        packageManager.getApplicationInfo(packageName, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        continue
                    }
                    
                    val name = packageManager.getApplicationLabel(appInfo).toString()
                    val icon = try {
                        packageManager.getApplicationIcon(packageName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                    
                    iconPacks.add(IconPack(packageName, name, icon))
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error querying category $category", e)
            }
        }
        
        return iconPacks.toList().sortedBy { it.name }
    }
    
    /**
     * Get themed icon for a specific app package
     */
    fun getThemedIcon(context: Context, iconPackPackage: String, targetPackage: String): Drawable? {
        if (iconPackPackage.isEmpty() || iconPackPackage == "system_default") {
            return null // Use system default
        }
        
        try {
            val iconPackContext = context.createPackageContext(iconPackPackage, Context.CONTEXT_IGNORE_SECURITY)
            val iconPackResources = iconPackContext.resources
            
            // Try different common naming patterns for icon resources
            val possibleNames = listOf(
                targetPackage,
                targetPackage.replace(".", "_"),
                "${targetPackage}_icon",
                "ic_${targetPackage}",
                "ic_${targetPackage.replace(".", "_")}",
                targetPackage.substringAfterLast("."),
                "ic_${targetPackage.substringAfterLast(".")}"
            )
            
            for (name in possibleNames) {
                try {
                    val resourceId = iconPackResources.getIdentifier(name, "drawable", iconPackPackage)
                    if (resourceId != 0) {
                        return ContextCompat.getDrawable(iconPackContext, resourceId)
                    }
                } catch (e: Exception) {
                    // Continue to next name
                }
            }
            
            // Try to find in appfilter.xml (common in icon packs)
            try {
                val appFilterId = iconPackResources.getIdentifier("appfilter", "xml", iconPackPackage)
                if (appFilterId != 0) {
                    // This would require XML parsing, which is complex
                    // For now, we'll skip this advanced feature
                }
            } catch (e: Exception) {
                // Ignore
            }
            
        } catch (e: Exception) {
            Log.w(TAG, "Error getting themed icon for $targetPackage from $iconPackPackage", e)
        }
        
        return null
    }
}