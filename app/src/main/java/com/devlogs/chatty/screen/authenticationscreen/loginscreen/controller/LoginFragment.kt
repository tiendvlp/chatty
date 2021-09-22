package com.devlogs.chatty.screen.authenticationscreen.loginscreen.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.authenticationscreen.AuthenticationScreenNavigator
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.LoginMvcView
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view.getLoginMvcView
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationAction.*
import com.devlogs.chatty.screen.authenticationscreen.loginscreen.state.LoginPresentationState.*
import com.devlogs.chatty.screen.common.mvcview.MvcViewFactory
import com.devlogs.chatty.screen.common.presentationstate.*
import com.devlogs.chatty.screen.common.presentationstate.CommonPresentationAction.InitAction
import com.devlogs.chatty.screen.mainscreen.MainActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(), LoginMvcView.Listener, PresentationStateChangedListener {

    companion object {
        fun getInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    private lateinit var mMvcView: LoginMvcView
    @Inject lateinit var presentationStateManager: PresentationStateManager
    @Inject lateinit var mMvcViewFactory: MvcViewFactory
    @Inject lateinit var navigator: AuthenticationScreenNavigator
    @Inject lateinit var loginController: LoginController
    @Inject lateinit var silentlyController: LoginSilentlyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presentationStateManager.init(savedInstanceState, NotLoggedInState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        mMvcView = mMvcViewFactory.getLoginMvcView(container)
        return mMvcView.getRootView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        presentationStateManager.onSavedInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        mMvcView.register(this)
        presentationStateManager.register(this, true)
    }

    override fun onStop() {
        super.onStop()
        mMvcView.unRegister(this)
        presentationStateManager.unRegister(this)
        loginController.cancel()
    }

    override fun onStateChanged(
            previousState: PresentationState?,
            currentState: PresentationState,
            action: PresentationAction
    ) {
        when (currentState) {
            is NotLoggedInState -> {
                if (action is InitAction) {
                    normalLog("Init Action")
                    presentationStateManager.consumeAction(LoginSilentlyAction)
                }
                if (action is LoginFailedAction) mMvcView.loginFailed(action.errorMessage)
            }
            is LoginSuccessState -> {
                normalLog("Login Success2")
                normalLog("Subscribe to: " + currentState.id)
                Firebase.messaging.subscribeToTopic(currentState.id)
                    .addOnCanceledListener {
                        normalLog("Subscribe has been canceled")
                    }
                    .addOnSuccessListener {
                        normalLog("Subscribe has been failed")
                    }
                    .addOnCompleteListener { task ->
                        var message = "subscribe failed" + task.getResult();
                        if (task.isSuccessful) {
                            message = "subscribe success ${currentState.id.isEmpty()}"
                        }
                        this@LoginFragment.normalLog(message)
                    }
                mMvcView.loginSuccess()
                MainActivity.start(requireContext())
            }
            is LoadingState -> {
                normalLog("Loading State")
                if (action is LoginAction) {
                    loginController.login(action.email, action.password)
                } else if (action is LoginSilentlyAction) {
                    silentlyController.silentLogin()
                }
                mMvcView.loading()
            }
        }
    }

    private var email = ""
    override fun onBtnLoginClicked(email: String, password: String) {
        presentationStateManager.consumeAction(LoginAction(email, password))
    }

    override fun onBtnRegisterClicked() {
        navigator.navigateToRegisterScreen()
    }

    override fun onBtnForgotPasswordClicked() {
        Toast.makeText(requireContext(), "NOT SUPPORTED YET", Toast.LENGTH_SHORT).show()
    }
}
