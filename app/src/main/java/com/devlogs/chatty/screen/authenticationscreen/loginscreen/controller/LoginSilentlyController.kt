package com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller

import com.devlogs.chatty.login.SilentLoginUseCase
import com.devlogs.chatty.login.SilentLoginUseCase.Result
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.LoginFailedAction
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.LoginSuccessAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginSilentlyController {
    private val mSilentLoginUseCase : SilentLoginUseCase
    private var mPresentationStateManager : PresentationStateManager
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    @Inject
    constructor(silentLoginUseCase: SilentLoginUseCase, presentationStateManager: PresentationStateManager) {
        mSilentLoginUseCase = silentLoginUseCase
        mPresentationStateManager = presentationStateManager
    }

    fun silentLogin () {
        mPresentationStateManager.consumeAction(LoginPresentationAction.LoginAction("", ""))
        scope.launch {
            val result = mSilentLoginUseCase.execute()
            delay(1500)
            if (result is Result.Allow) {
                mPresentationStateManager.consumeAction(LoginSuccessAction)
                return@launch
            }

            mPresentationStateManager.consumeAction(LoginFailedAction(""))
        }

    }
}