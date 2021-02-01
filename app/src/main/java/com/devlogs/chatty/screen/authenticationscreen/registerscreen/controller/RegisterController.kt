package com.devlogs.chatty.screen.authenticationscreen.registerscreen.controller

import com.devlogs.chatty.login.LoginWithEmailUseCaseSync
import com.devlogs.chatty.register.RegisterByEmailUseCaseSync
import com.devlogs.chatty.register.RegisterByEmailUseCaseSync.Result.*
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.state.RegisterPresentationAction.RegisterSuccessAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterController {
    private val mRegisterUseCase : RegisterByEmailUseCaseSync
    private val mLoginUseCase : LoginWithEmailUseCaseSync
    private val mPresentationStateManager: PresentationStateManager
    private val mainScope = CoroutineScope(Dispatchers.Main.immediate)

    @Inject
    constructor(registerByEmailUseCaseSync: RegisterByEmailUseCaseSync,loginWithEmailUseCaseSync: LoginWithEmailUseCaseSync, presentationStateManager: PresentationStateManager) {
        mRegisterUseCase = registerByEmailUseCaseSync
        mLoginUseCase = loginWithEmailUseCaseSync
        mPresentationStateManager = presentationStateManager
    }

    fun register (email: String, password: String) {
        mainScope.launch {
            val result = mRegisterUseCase.execute(email, password)

            if (result is Success) {
                mPresentationStateManager.consumeAction(RegisterSuccessAction)
                login(email, password)
            }
            if (result is GeneralError) {

            }
            if (result is NetworkError) {

            }
            if (result is UserAlreadyExistError) {

            }
        }
    }

    private fun login (email: String ,password: String) {

    }
}