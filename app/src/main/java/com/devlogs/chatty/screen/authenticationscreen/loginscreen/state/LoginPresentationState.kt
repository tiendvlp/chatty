package com.devlogs.chatty.screen.authenticationscreen.loginscreen.state

import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.*
import com.devlogs.chatty.screen.common.presentationstate.*
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.InitAction

sealed class LoginPresentationState : PresentationState {
    override val allowSave: Boolean = true
    override fun getTag(): String {
        return "LoginPresentationState"
    }

    object NotLoggedInState : LoginPresentationState() {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): CauseAndEffect {
            when (action) {
                is InitAction -> return CauseAndEffect(action, this)
                is LoginAction ->  return CauseAndEffect(action, LoadingState(action.email, action.password))
                is LoginSilentlyAction -> return CauseAndEffect(action, LoadingState("", ""))
            }
                return super.consumeAction(previousState, action)
        }
    }

    data class LoadingState (val inputEmail: String, val inputPassword: String) : LoginPresentationState() {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): CauseAndEffect {
            when (action) {
                is LoginFailedAction ->  return CauseAndEffect(action, NotLoggedInState)
                is LoginSuccessAction ->  return CauseAndEffect(action, LoginSuccessState)
            }
            return super.consumeAction(previousState, action)
        }
    }

    object LoginSuccessState : LoginPresentationState () {
        override fun consumeAction(previousState: PresentationState, action: PresentationAction): CauseAndEffect {
            return super.consumeAction(previousState, action)
        }
    }
}
