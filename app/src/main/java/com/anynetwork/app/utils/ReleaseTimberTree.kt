package com.anynetwork.app.utils

import timber.log.Timber

class ReleaseTimberTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // do nothing
    }
}