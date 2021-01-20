package com.devlogs.chatty.screen.common.presentationstate

import android.os.Bundle
import com.devlogs.chatty.common.base.BaseObservable

class PresentationStateManager : BaseObservable<PresentationStateChangedListener>() {
    private lateinit var currentState: PresentationState
    private lateinit var currentAction : PresentationAction
    private lateinit var previousState : PresentationState

    fun init(savedInstanceState: Bundle?, defaultState: PresentationState) {
            previousState = defaultState
        if (savedInstanceState != null && savedInstanceState.containsKey(defaultState.getTag())) {
            currentAction = CommonPresentationAction.RestoreAction
            currentState = savedInstanceState.getSerializable(defaultState.getTag()) as PresentationState
        } else {
            currentAction = CommonPresentationAction.InitAction
            currentState = defaultState
        }
        getListener().forEach {
            it.onStateChanged(previousState, currentState, currentAction)
        }
    }

    fun consumeAction (action: PresentationAction) {
        previousState = currentState
        currentState = currentState.consumeAction(action)
        currentAction = action

        getListener().forEach {
            it.onStateChanged(previousState, currentState, action)
        }
    }

    override fun onFirstListenerRegistered() {
        super.onFirstListenerRegistered()
         currentState.let {
             getListener().forEach {
                 it.onStateChanged(previousState, currentState, currentAction)
             }
         }
    }

    fun onSavedInstanceState (outState: Bundle) {
        outState.putSerializable(currentState.getTag(), currentState)
    }
}