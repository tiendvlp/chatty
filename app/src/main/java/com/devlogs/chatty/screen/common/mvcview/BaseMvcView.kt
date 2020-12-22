package com.devlogs.chatty.screen.common.mvcview

import android.content.Context
import android.view.View

abstract class BaseMvcView <LISTENER> : BaseObservableMvcView<LISTENER>(), MvcView {
    private lateinit var mRootView : View

    public fun setRootView (rootView: View) {
        mRootView = rootView
    }

    override fun getRootView(): View {
        return mRootView
    }

    public fun getContext () : Context {
        return mRootView.context
    }

    public fun <TYPE: View> findViewById (id : Int) : TYPE {
        return mRootView.findViewById(id)
    }
}