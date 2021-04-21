package com.devlogs.chatty.screen.common.presentationstate

import java.io.Serializable

interface PresentationState : Serializable {
    fun getTag () : String
    fun consumeAction (previousState: PresentationState ,action: PresentationAction) : PresentationState
}

