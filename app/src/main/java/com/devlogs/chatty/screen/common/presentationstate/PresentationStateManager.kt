package com.devlogs.chatty.screen.common.presentationstate

import android.os.Bundle
import com.devlogs.chatty.common.base.BaseObservable
import com.devlogs.chatty.common.helper.normalLog

class PresentationStateManager : BaseObservable<PresentationStateChangedListener>() {
    lateinit var currentState: PresentationState private set
    private lateinit var currentAction : PresentationAction
    private var previousState : PresentationState? = null

    fun init(savedInstanceState: Bundle?, defaultState: PresentationState) {
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
        currentState = currentState.consumeAction(currentState,action)
        currentAction = action

        getListener().forEach {
            it.onStateChanged(previousState, currentState, action)
        }
    }

    fun register (listener: PresentationStateChangedListener, getPreviousEvent: Boolean) {
        register(listener)
        if (getPreviousEvent && previousState != null) {
            normalLog("GetFired from previous event")
            listener.onStateChanged(previousState, currentState, CommonPresentationAction.RestoreAction)
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
        if (currentState.allowSave) {
            outState.putSerializable(currentState.getTag(), currentState)
        }
    }
}