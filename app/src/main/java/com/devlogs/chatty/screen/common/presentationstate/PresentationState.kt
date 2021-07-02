package com.devlogs.chatty.screen.common.presentationstate

import java.io.Serializable

interface PresentationState : Serializable {
    val allowSave : Boolean

    fun getTag () : String {
        return javaClass.simpleName
    }

    fun consumeAction (previousState: PresentationState ,action: PresentationAction) : CauseAndEffect {
        throw InvalidActionException("${getTag()}", action.toString())
    }
}

