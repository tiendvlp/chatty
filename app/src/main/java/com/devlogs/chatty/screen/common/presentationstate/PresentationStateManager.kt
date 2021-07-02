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
            normalLog("Restore")
            currentAction = CommonPresentationAction.RestoreAction
            currentState = savedInstanceState.getSerializable(defaultState.getTag()) as PresentationState
        } else {
            currentAction = CommonPresentationAction.InitAction
            currentState = defaultState
        }

        consumeAction(currentAction)
//        getListener().forEach {
//            it.onStateChanged(previousState, currentState, currentAction)
//        }
    }

    fun consumeAction (action: PresentationAction) {
        normalLog("Consum action: ${action.javaClass.simpleName} at state: ${currentState.javaClass.simpleName}")
        previousState = currentState
        val causeAndEffect = currentState.consumeAction(previousState!!, action)
        currentState = causeAndEffect.state
        currentAction = causeAndEffect.action
        getListener().forEach {
            it.onStateChanged(previousState, currentState, currentAction)
        }
    }

    fun register (listener: PresentationStateChangedListener, getPreviousEvent: Boolean) {
        register(listener)
        if (getPreviousEvent && previousState != null) {
            listener.onStateChanged(previousState, currentState, currentAction)
        }
    }

    fun onSavedInstanceState (outState: Bundle) {
        if (currentState.allowSave) {
            outState.putSerializable(currentState.getTag(), currentState)
        }
    }
}