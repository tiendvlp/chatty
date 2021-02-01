package com.devlogs.chatty.screen.authenticationscreen.registerscreen.state

import com.devlogs.chatty.screen.common.presentationstate.PresentationAction

sealed class RegisterPresentationAction : PresentationAction {

    data class RegisterAction (val email: String, val password: String): RegisterPresentationAction()
    object RegisterSuccessAction : RegisterPresentationAction()
    data class RegisterFailedAction (val errMessage: String): RegisterPresentationAction()
}