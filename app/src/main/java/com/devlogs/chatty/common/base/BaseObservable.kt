package com.devlogs.chatty.common.base

import java.util.*
import kotlin.collections.HashSet

abstract class BaseObservable<LISTENER> : Observable<LISTENER> {

    private val MONITOR = Any()
    private val mListeners: HashSet<LISTENER> = HashSet()


    override fun register(listener: LISTENER) {
        synchronized(MONITOR) {
            val hadNoListener : Boolean = mListeners.size == 0
            mListeners.add(listener)

            if (hadNoListener && mListeners.size == 1) {
                onFirstListenerRegistered()
            }
        }
    }

    override fun unRegister(listener: LISTENER) {
        synchronized(MONITOR) {
            val hadOneListener = mListeners.size == 1
            mListeners.remove(listener)
            if (hadOneListener && mListeners.size == 0) {
                onLastListenerUnregistered()
            }
        }
    }

    protected fun getListener () : Set<LISTENER> {
        synchronized(MONITOR) {
            return Collections.unmodifiableSet(java.util.HashSet(mListeners))
        }
    }

    open protected fun onFirstListenerRegistered () {

    }

    open protected fun onLastListenerUnregistered () {

    }
}