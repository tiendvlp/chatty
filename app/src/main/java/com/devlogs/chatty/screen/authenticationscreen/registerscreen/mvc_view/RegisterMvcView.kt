package com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view

import android.view.ViewGroup
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.mvcview.ObservableMvcView

interface RegisterMvcView : ObservableMvcView<RegisterMvcView.Listener> {
    interface Listener {
        fun onBtnRegisterClicked (email: String, password: String)
        fun onBtnSignInClicked ()
    }

    fun loading ()
    fun registerFailed (errorMessage: String)
    fun registerSuccess ()

}

fun MvcViewFactory.getRegisterMvcView (container: ViewGroup?) : RegisterMvcView = RegisterMvcViewImp(mLayoutInflater, container)
