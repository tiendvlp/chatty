package com.devlogs.chatty.common.helper

import android.util.Log

fun Any.normalLog (message: String) {
    Log.d(this.javaClass.simpleName, message)
}

fun Any.warningLog (message: String) {
    Log.w(this.javaClass.simpleName, message)
}

fun Any.errorLog (message: String) {
    Log.e(this.javaClass.simpleName, message)
}