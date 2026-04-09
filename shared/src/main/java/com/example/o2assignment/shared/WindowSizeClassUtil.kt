package com.example.o2assignment.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowSizeClass

/**
 * Returns the number of columns for the home screen grid based on the device type and orientation.
 *
 * @return 1 column for phone portrait, 2 for phone landscape,
 *         2 for tablet portrait, and 3 for tablet landscape.
 */
@Composable
fun WindowSizeClass.getHomeColumnCount(): Int {
    val isTablet = isTablet()

    val isMediumOrWider = this.isWidthAtLeastBreakpoint(600)
    val isExpandedOrWider = this.isWidthAtLeastBreakpoint(840)

    return when {
        isTablet -> if (isExpandedOrWider) 3 else 2
        else -> if (isMediumOrWider) 2 else 1
    }
}

/**
 * A composable function to determine if the device is a tablet based on screen width.
 *
 * @return `true` if the screen width is greater than 900dp, and height is
 * greater than 700dp, `false` otherwise.
 */
@Composable
private fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 900 && configuration.screenHeightDp >= 700
}