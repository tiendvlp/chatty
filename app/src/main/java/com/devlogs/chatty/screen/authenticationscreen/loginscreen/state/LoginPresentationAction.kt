package com.devlogs.chatty.screen.authenticationscreen.loginscreen.state

import com.devlogs.chatty.screen.common.presentationstate.PresentationAction

sealed class LoginPresentationAction : PresentationAction {
    data class LoginAction (val email: String, val password: String) : LoginPresentationAction()
    object LoginSilentlyAction: LoginPresentationAction()
    data class LoginFailedAction (val errorMessage: String) : LoginPresentationAction()
    data class LoginSuccessAction (val id: String) : LoginPresentationAction()
}