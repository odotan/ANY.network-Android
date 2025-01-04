package com.anynetwork.app.ui.utils

import android.content.Context
import android.content.res.Configuration

object AppConfig {

    var density = 1f
    var screenWidthDp = 1
    var fontDensity = 1f
    var widthPixels = 1
    var heightPixels = 1
    var xDpi = 1f

    fun onConfigChanged(context: Context, newConfiguration: Configuration?) {
        val configuration = newConfiguration ?: context.resources.configuration


        density = context.resources.displayMetrics.density
        screenWidthDp = configuration.screenWidthDp
        widthPixels = context.resources.displayMetrics.widthPixels
        heightPixels = context.resources.displayMetrics.heightPixels
        fontDensity = context.resources.displayMetrics.density
        xDpi = context.resources.displayMetrics.xdpi

        var lDensity = context.resources.displayMetrics.density
        var lScreenWidthDp = configuration.screenWidthDp
        var lWidthPx = context.resources.displayMetrics.widthPixels
        var lHeightPx = context.resources.displayMetrics.heightPixels
        var lScreenHeightDp =configuration.screenHeightDp
        var lYdpi =context.resources.displayMetrics.ydpi
        var lXdpi =context.resources.displayMetrics.xdpi
        var test =context.resources.displayMetrics.xdpi
    }
}