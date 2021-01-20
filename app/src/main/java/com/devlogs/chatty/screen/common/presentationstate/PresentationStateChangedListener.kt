package com.devlogs.chatty.screen.common.presentationstate

interface PresentationStateChangedListener {
    fun onStateChanged (previousState: PresentationState, currentState: PresentationState, action: PresentationAction)
}