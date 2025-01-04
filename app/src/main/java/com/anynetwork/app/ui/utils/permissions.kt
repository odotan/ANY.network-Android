package com.anynetwork.app.ui.utils

import android.content.Context

// Utility function to check if a permission is granted
fun checkSelfPermission(context: Context, permission: String): Boolean {
    return context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}