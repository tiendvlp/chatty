package com.devlogs.chatty.screen.chatscreen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.isAtLeastAndroid11
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    companion object {
        fun start (context: Context) {
            context.startActivity(Intent(context, ChatActivity::class.java))
        }
    }

    @Inject
    protected lateinit var navigator: ChatScreenNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_chat)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        navigator.init(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}