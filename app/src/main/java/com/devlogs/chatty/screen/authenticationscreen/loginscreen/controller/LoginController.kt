package com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller

import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.login.LoginWithEmailUseCaseSync.Result
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.*
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import kotlinx.coroutines.*
import javax.inject.Inject

class LoginController {
    private var mLoginWithEmailUseCase: LoginWithEmailUseCaseSync
    private var mPresentationStateManager : PresentationStateManager
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    @Inject
    constructor(loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync, presentationStateManager: PresentationStateManager) {
        mLoginWithEmailUseCase = loginWithEmailUseCaseSync
        mPresentationStateManager = presentationStateManager
    }

    fun login (email: String, password: String) {
        mPresentationStateManager.consumeAction(LoginAction(email, password))

        scope.launch {
            val result = mLoginWithEmailUseCase.execute(email, password)
            if (result is Result.NetworkError) mPresentationStateManager.consumeAction(LoginFailedAction("Network error, please check your internet connection"))
            if (result is Result.GeneralError) mPresentationStateManager.consumeAction(LoginFailedAction("Login Failed"))
            if (result is Result.InvalidAccountError) mPresentationStateManager.consumeAction(LoginFailedAction("Your account doesn't exist"))
            if (result is Result.Success) mPresentationStateManager.consumeAction(LoginSuccessAction)
        }
    }

    fun cancel () {
        scope.coroutineContext.cancelChildren()
    }
}