package com.devlogs.chatty.screen.common.mvcview

import android.content.Context
import android.view.LayoutInflater
import android.view.Window

data class UIToolkit (
    val window: Window,
    val context: Context,
    val layoutInflater: LayoutInflater
        )
