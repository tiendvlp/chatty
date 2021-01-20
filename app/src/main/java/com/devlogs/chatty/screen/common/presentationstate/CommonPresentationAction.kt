package com.devlogs.chatty.screen.common.presentationstate

sealed class CommonPresentationAction : PresentationAction {
    object InitAction : CommonPresentationAction()
    object RestoreAction : CommonPresentationAction ()
}