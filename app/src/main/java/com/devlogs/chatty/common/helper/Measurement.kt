package com.devlogs.chatty.common.helper

import android.content.Context

fun convertDpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}