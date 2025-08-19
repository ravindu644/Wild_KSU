package com.rifsxd.ksunext.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.rifsxd.ksunext.R
import java.util.*

/**
 * Utility object for icon-related operations across the application.
 * Provides helper functions for seasonal icons, icon type management, and icon selection.
 */
object IconUtils {
    
    /**
     * Gets the appropriate seasonal icon based on the current month.
     * 
     * @return ImageVector representing the current season
     */
    fun getSeasonalIcon(): ImageVector {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.MONTH)) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> Icons.Filled.AcUnit // Winter
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> Icons.Filled.Spa // Spring
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> Icons.Filled.WbSunny // Summer
            else -> Icons.Filled.Forest // Fall
        }
    }
    
    /**
     * Gets the name of the current season for display purposes.
     * 
     * @return String name of the current season
     */
    fun getSeasonalIconName(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.MONTH)) {
            Calendar.DECEMBER, Calendar.JANUARY, Calendar.FEBRUARY -> "Winter"
            Calendar.MARCH, Calendar.APRIL, Calendar.MAY -> "Spring"
            Calendar.JUNE, Calendar.JULY, Calendar.AUGUST -> "Summer"
            else -> "Fall"
        }
    }
    
    /**
     * Gets an icon based on the specified icon type.
     * Returns either an ImageVector or a Painter resource.
     * 
     * @param iconType The type of icon to retrieve
     * @return Any - either ImageVector or Painter depending on the icon type
     */
    @Composable
    fun getIcon(iconType: String): Any {
        return when (iconType) {
            "OFF" -> Icons.Filled.VisibilityOff
            "SEASONAL" -> getSeasonalIcon()
            "WINTER" -> Icons.Filled.AcUnit
            "SPRING" -> Icons.Filled.Spa
            "SUMMER" -> Icons.Filled.WbSunny
            "FALL" -> Icons.Filled.Forest
            "KSU_NEXT" -> painterResource(R.drawable.ic_ksu_next)
            "CANNABIS" -> painterResource(R.drawable.ic_cannabis)
            "AMOGUS_SUSFS" -> painterResource(R.drawable.ic_sus)
            else -> getSeasonalIcon()
        }
    }
    
    /**
     * Gets the display name for an icon type.
     * 
     * @param iconType The icon type
     * @return Human-readable name for the icon type
     */
    fun getIconTypeName(iconType: String): String {
        return when (iconType) {
            "OFF" -> "Hidden"
            "SEASONAL" -> "Seasonal (${getSeasonalIconName()})"
            "WINTER" -> "Winter"
            "SPRING" -> "Spring"
            "SUMMER" -> "Summer"
            "FALL" -> "Fall"
            "KSU_NEXT" -> "KSU Next"
            "CANNABIS" -> "Cannabis"
            "AMOGUS_SUSFS" -> "Sus"
            else -> "Seasonal (${getSeasonalIconName()})"
        }
    }
    
    /**
     * Gets all available icon types.
     * 
     * @return List of available icon type strings
     */
    fun getAvailableIconTypes(): List<String> {
        return listOf(
            "SEASONAL",
            "WINTER",
            "SPRING",
            "SUMMER",
            "FALL",
            "KSU_NEXT",
            "CANNABIS",
            "AMOGUS_SUSFS",
            "OFF"
        )
    }
    
    /**
     * Checks if an icon type uses a drawable resource (vs ImageVector).
     * 
     * @param iconType The icon type to check
     * @return true if the icon type uses a drawable resource
     */
    fun isDrawableIcon(iconType: String): Boolean {
        return when (iconType) {
            "KSU_NEXT", "CANNABIS", "AMOGUS_SUSFS" -> true
            else -> false
        }
    }
}