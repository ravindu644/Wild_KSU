package com.rifsxd.ksunext.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import com.rifsxd.ksunext.ui.theme.LocalUIBlur

/**
 * Applies UI blur effect that works harmoniously with UI transparency.
 * The blur is applied more subtly to complement the transparency effect
 * rather than overwhelming it. This creates a glass-like effect when
 * combined with UI transparency.
 */
@Composable
fun Modifier.applyUIBlur(): Modifier {
    val uiBlur = LocalUIBlur.current
    return if (uiBlur > 0f) {
        // Apply blur with reduced intensity for better integration with transparency
        // The blur radius is scaled down to create a more subtle effect
        this.blur(radius = (uiBlur * 0.3f).dp)
    } else {
        this
    }
}

/**
 * Applies custom blur effect with specified radius
 */
fun Modifier.applyBlur(blurRadius: Float): Modifier {
    return if (blurRadius > 0f) {
        this.blur(blurRadius.dp)
    } else {
        this
    }
}