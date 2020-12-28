package com.devlogs.chatty.screen.launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.mainserver.user.UserMainGraphqlApiImp
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.*
import mainserver.GetUserByEmailQuery
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    @Inject
    lateinit var loginUseCase : LoginWithEmailUseCaseSync
    @Inject
    lateinit var userMainGraphqlApiImp: UserMainGraphqlApiImp
    private val scopeJob = Job()
    private val coroutineScope = CoroutineScope(scopeJob + Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_launch)


        coroutineScope.launch {
            userMainGraphqlApiImp.getUser("tiendvlp2@reply.com")
            loginUseCase.execute("mingting15@mintin.com", "tiendvlp")
        }
    }
}