package com.devlogs.chatty.screen.launch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devlogs.chatty.R
import com.devlogs.chatty.channel.GetUserChannelsOverPeriodOfTimeUseCaseSync
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.mainserver.channel.ChannelMainServerApiImp
import com.devlogs.chatty.datasource.mainserver.user.UserMainServerApiImp
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject


@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    @Inject
    lateinit var loginUseCase : LoginWithEmailUseCaseSync
    @Inject
    lateinit var userMainServerApiImp: UserMainServerApiImp
    @Inject
    lateinit var messageApiImp : MessageMainServerApi
    @Inject
    lateinit var channelMainServerApi: ChannelMainServerApiImp
    @Inject
    lateinit var getUserChannelUSeCaseOverPeriodOfTime : GetUserChannelsOverPeriodOfTimeUseCaseSync
    private val scopeJob = Job()
    private val coroutineScope = CoroutineScope(scopeJob + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_launch)


    }
}