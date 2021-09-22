package com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller

import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.login.SilentLoginUseCase
import com.devlogs.chatty.login.SilentLoginUseCase.Result
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.LoginFailedAction
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.LoginSuccessAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.user.GetAccountUseCase
import com.devlogs.chatty.user.GetUserByEmailUseCaseSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginSilentlyController {
    private val mSilentLoginUseCase : SilentLoginUseCase
    private var mPresentationStateManager : PresentationStateManager
    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    private val getAccountUseCase: GetAccountUseCase;

    @Inject
    constructor(
        silentLoginUseCase: SilentLoginUseCase,
        presentationStateManager: PresentationStateManager,
        getAccountUseCase: GetAccountUseCase
    ) {
        mSilentLoginUseCase = silentLoginUseCase
        mPresentationStateManager = presentationStateManager
        this.getAccountUseCase = getAccountUseCase
    }

    fun silentLogin () {
        scope.launch {
            val result = mSilentLoginUseCase.execute()
            delay(1500)
            if (result is Result.Allow) {
                val getAccountResult = getAccountUseCase.execute()
                if (getAccountResult is GetAccountUseCase.Result.Success) {
                    normalLog("getAccountsuccess: " + getAccountResult.accountEntity)
                    mPresentationStateManager.consumeAction(LoginSuccessAction(getAccountResult.accountEntity.id))
                }
                return@launch
            }

            mPresentationStateManager.consumeAction(LoginFailedAction(""))
        }

    }
}