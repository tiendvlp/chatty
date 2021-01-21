package com.devlogs.chatty.screen.authenticationscreen.loginscreen.state

import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.*
import com.devlogs.chatty.screen.common.presentationstate.InvalidActionException
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState

sealed class LoginPresentationState : PresentationState {
    override fun getTag(): String {
        return "LoginPresentationState"
    }

    data class NotLoggedInState (val inputEmail : String, val inputPassword: String) : LoginPresentationState() {
        override fun consumeAction(action: PresentationAction): PresentationState {
            when (action) {
                is LoginAction ->  return LoadingState(inputEmail, inputPassword)
                is LoginSuccessAction ->  return LoginSuccessState
            }
               throw InvalidActionException("${getTag()}.NotLoggedInState", action.toString())
        }
    }

    data class LoadingState (val inputEmail: String, val inputPassword: String) : LoginPresentationState() {
        override fun consumeAction(action: PresentationAction): PresentationState {
            when (action) {
                is LoginFailedAction ->  return NotLoggedInState(inputEmail, inputPassword)
                is LoginSuccessAction ->  return LoginSuccessState
            }
            throw InvalidActionException("${getTag()}.LoadingState", action.toString())
        }
    }

    object LoginSuccessState : LoginPresentationState () {
        override fun consumeAction(action: PresentationAction): PresentationState {
            throw InvalidActionException("${getTag()}.LoginSuccessState", action.toString())
        }
    }
}