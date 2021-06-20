package com.devlogs.chatty.screen.common.presentationstate

import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.InitAction
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.RestoreAction
import java.io.Serializable

interface PresentationState : Serializable {
    val allowSave : Boolean
    fun getTag () : String {
        return javaClass.simpleName
    }

    fun consumeAction (previousState: PresentationState ,action: PresentationAction) : PresentationState {
        if (action is RestoreAction) {
            return this
        }
        if (action is InitAction) {
            return this
        }
        throw InvalidActionException("${getTag()}", action.toString())
    }
}

