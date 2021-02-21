package com.devlogs.chatty.screen.authenticationscreen.registerscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.devlogs.chatty.screen.authenticationscreen.AuthenticationScreenNavigator
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.RegisterMvcView
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view.getRegisterMvcView
import com.devlogs.chatty.screen.authenticationscreen.registerscreen.state.RegisterPresentationAction.RegisterAction
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateChangedListener
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(), RegisterMvcView.Listener, PresentationStateChangedListener {

    companion object {
        fun getInstance () : RegisterFragment {
            return RegisterFragment()
        }
    }

    @Inject
    lateinit var presentationStateManager : PresentationStateManager
    @Inject
    lateinit var navigator: AuthenticationScreenNavigator
    @Inject
    lateinit var mvcViewFactory: MvcViewFactory

    private lateinit var mMvcView : RegisterMvcView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        presentationStateManager.init(savedInstanceState, )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mMvcView = mvcViewFactory.getRegisterMvcView(container)
        return mMvcView.getRootView()
    }

    override fun onStart() {
        mMvcView.register(this)
        presentationStateManager.register(this)
        super.onStart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presentationStateManager.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        mMvcView.unRegister(this)
        presentationStateManager.unRegister(this)
        super.onStop()
    }

    override fun onStateChanged(
        previousState: PresentationState,
        currentState: PresentationState,
        action: PresentationAction
    ) {

    }

    override fun onBtnRegisterClicked(email: String, password: String) {
        presentationStateManager.consumeAction(RegisterAction(email, password))
    }

    override fun onBtnSignInClicked() {
        navigator.navigateBack()
    }

}