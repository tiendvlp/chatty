package com.devlogs.chatty.screen.authenticationscreen.registerscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.R
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.screen.authenticationscreen.AuthenticationScreenNavigator
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcView
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(), RegisterMvcView.Listener {

    companion object {
        fun getInstance () : RegisterFragment {
            return RegisterFragment()
        }
    }

    @Inject
    lateinit var navigator: AuthenticationScreenNavigator
    @Inject
    lateinit var mvcViewFactory: MvcViewFactory

    private lateinit var mMvcView : RegisterMvcView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mMvcView = mvcViewFactory.getRegisterMvcView(container)
        return mMvcView.getRootView()
    }

    override fun onStart() {
        mMvcView.register(this)
        super.onStart()
    }

    override fun onStop() {
        mMvcView.unRegister(this)
        super.onStop()
    }

    override fun onBtnRegisterClicked() {
    }

    override fun onBtnSignInClicked() {
        // go back to login screen
        navigator.navigateBack()
    }
}