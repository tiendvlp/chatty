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

    val refreshtk = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1pbmd0aW5nMTVAbWludGluLmNvbSIsImlkIjoiNWY3NWNkMzUzYTQwNGQzMzY4YTlkOTQxIiwiaWF0IjoxNjEwMzQ2NTUyLCJleHAiOjE2MTA5NTEzNTJ9.Bwwf0fGX2-Vs5dEXjyd5p7kNFKeojMR1pCw6-BEvPAc"

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

        coroutineScope.launch(Dispatchers.IO) {
//            loginUseCase.execute("mingting15@mintin.com", "tiendvlp")
//            userMainServerApiImp.getUser("mingting15@mintin.com")
            (getUserChannelUSeCaseOverPeriodOfTime.execute(10, 10) as GetUserChannelsOverPeriodOfTimeUseCaseSync.Result.Success)
                .channels.forEach {
                    normalLog("RESULT: " + it.admin)
                    normalLog("RESULT: " + it.admin)
                }
//            messageApiImp.getChannelMessage("5fb8bdf0614d57292085e59d", 10)
//            messageApiImp.sendTextMessage("Hi hi tui ne", "5fb8bdf0614d57292085e59d")
        }
    }
}