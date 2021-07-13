package com.devlogs.chatty.screen.chatscreen

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.isAtLeastAndroid11
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL_ID_PARAM = "CHANNEL_ID"
        fun start (context: Context, channelId: String) {
            assert(channelId.isNotBlank())
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHANNEL_ID_PARAM, channelId)
            context.startActivity(intent)
        }
    }


    @Inject
    protected lateinit var navigator: ChatScreenNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_chat)
        val channelId = intent.extras!!.get(CHANNEL_ID_PARAM) as String
        WindowCompat.setDecorFitsSystemWindows(window, false)
        navigator.init(channelId,savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        normalLog("ChatActivity get destroyed")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
}