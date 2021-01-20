package com.devlogs.chatty.screen.common.mvcview

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.LoginMvcView
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.LoginMvcViewImp
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcView
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcViewImp


class MvcViewFactory {
    private val mLayoutInflater: LayoutInflater

    constructor(layoutInflater: LayoutInflater) {
        mLayoutInflater = layoutInflater
    }

    fun getLoginMvcView (container: ViewGroup?) : LoginMvcView {
        return LoginMvcViewImp(mLayoutInflater, container)
    }

    fun getRegisterMvcView (container: ViewGroup?) : RegisterMvcView {
        return RegisterMvcViewImp(mLayoutInflater, container)
    }
}