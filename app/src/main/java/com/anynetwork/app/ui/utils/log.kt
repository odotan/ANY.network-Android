package com.anynetwork.app.ui.utils

import timber.log.Timber

inline fun <T> T.log(block: () -> String): T {
    Timber.i("${block()}${": $this"}")
    return this
}
