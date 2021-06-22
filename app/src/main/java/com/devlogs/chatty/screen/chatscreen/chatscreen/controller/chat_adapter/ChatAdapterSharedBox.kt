package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.devlogs.chatty.R

class ChatAdapterSharedBox {
    val backgroundRoundedTop: Drawable
    val backgroundRoundedBottom: Drawable
    val backgroundRoundedRight: Drawable
    val backgroundRoundedAll : Drawable

    val oppositeTextColor: ColorStateList
    val originTextColor: ColorStateList

    constructor(context: Context) {
        backgroundRoundedBottom = ContextCompat.getDrawable(context, R.drawable.circularview_bottomrightcornerbg)!!
        backgroundRoundedAll = ContextCompat.getDrawable(context,R.drawable.circularviewbg_a)!!
        backgroundRoundedRight = ContextCompat.getDrawable(context,R.drawable.circularview_rightcornersbg)!!
        backgroundRoundedTop = ContextCompat.getDrawable(context,R.drawable.circularview_toprightcornerbg)!!

        oppositeTextColor = context.getColorStateList(R.color.grey_200)
        originTextColor =  context.getColorStateList(R.color.main_color)

    }
}