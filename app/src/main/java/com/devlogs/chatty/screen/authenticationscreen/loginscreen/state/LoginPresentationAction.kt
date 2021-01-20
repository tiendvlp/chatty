package com.devlogs.chatty.screen.authenticationscreen.loginscreen.state

import com.devlogs.chatty.screen.common.presentationstate.PresentationAction

sealed class LoginPresentationAction : PresentationAction {
    data class LoginAction (val email: String, val password: String) : LoginPresentationAction()
    data class LoginFailedAction (val errorMessage: String) : LoginPresentationAction()
    object LoginSuccessAction : LoginPresentationAction()
}