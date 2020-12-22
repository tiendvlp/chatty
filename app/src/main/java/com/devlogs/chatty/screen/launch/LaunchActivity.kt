package com.devlogs.chatty.screen.launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devlogs.chatty.R
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@AndroidEntryPoint
class LaunchActivity : AppCompatActivity() {

    @Inject
    lateinit var loginUseCase : LoginWithEmailUseCaseSync

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_launch)

        loginUseCase.execute("mingting15@mintin.com", "tiendvlp")
            .subscribeBy(
                onError = {

                },
                onComplete = {

                }
            )
    }
}