package com.devlogs.chatty.screen.chatscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.devlogs.chatty.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    @Inject
    protected lateinit var navigator: ChatScreenNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_chat)
        navigator.init(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}