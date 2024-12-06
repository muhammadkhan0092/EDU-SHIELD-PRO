package com.example.edushieldpro.ui.utils

import android.content.Context
import android.view.View

object CommonFunctions {
    fun getViewSizeInDP(view: View, context: Context): Pair<Float, Float> {
        // Get the width and height in pixels
        val widthInPixels = view.width.toFloat()
        val heightInPixels = view.height.toFloat()

        // Get the screen density (density scale)
        val density = context.resources.displayMetrics.density

        // Convert pixels to DP
        val widthInDP = widthInPixels / density
        val heightInDP = heightInPixels / density

        return Pair(widthInDP, heightInDP)
    }
}