package com.devlogs.chatty.common.helper

import android.os.Build

fun isAtLeastAndroid11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
