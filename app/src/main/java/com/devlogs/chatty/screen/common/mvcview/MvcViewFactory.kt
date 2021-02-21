package com.devlogs.chatty.screen.common.mvcview

import android.view.LayoutInflater
import android.view.ViewGroup
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.LoginMvcView
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.LoginMvcViewImp
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcView
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcViewImp
import com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view.AccountMvcView
import com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view.AccountMvcViewImp


/**
 * all factory_code is extension-method, they were splited into XXXMvcView File
 * */

class MvcViewFactory {
      val mLayoutInflater: LayoutInflater

    constructor(layoutInflater: LayoutInflater) {
        mLayoutInflater = layoutInflater
    }

    fun getLayoutInflater () : LayoutInflater {
        return mLayoutInflater
    }

}