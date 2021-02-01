package com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller

import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync.Result
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.*
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import kotlinx.coroutines.*
import javax.inject.Inject

class LoginController {
    private var mLoginWithEmailUseCase: LoginWithEmailUseCaseSync
    private var mPresentationStateManager: PresentationStateManager

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    @Inject
    constructor(
        loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync,
        presentationStateManager: PresentationStateManager
    ) {
        mLoginWithEmailUseCase = loginWithEmailUseCaseSync
        mPresentationStateManager = presentationStateManager
    }

    fun login(email: String, password: String) {
        scope.launch {
            try {
                val result = mLoginWithEmailUseCase.execute(email, password)
                if (result is Result.NetworkError) mPresentationStateManager.consumeAction(
                    LoginFailedAction("Network error, please check your internet connection")
                )
                if (result is Result.GeneralError) mPresentationStateManager.consumeAction(
                    LoginFailedAction("Login Failed")
                )
                if (result is Result.InvalidAccountError) mPresentationStateManager.consumeAction(
                    LoginFailedAction("Your account doesn't exist")
                )
                if (result is Result.Success) mPresentationStateManager.consumeAction(LoginSuccessAction)

            } catch (e: CancellationException) {
                normalLog("Login Canceled ")
                mPresentationStateManager.consumeAction(LoginFailedAction("Canceled"))
            }
        }
    }

    fun cancel() {
        scope.coroutineContext.cancelChildren()
    }
}