package com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import com.devlogs.chatty.R
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.isEmail
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.ProgressButtonMvcView
import kotlinx.coroutines.*

class LoginMvcViewImp : BaseMvcView<LoginMvcView.Listener>, LoginMvcView {
    private val mainScope = CoroutineScope(Dispatchers.Main.immediate)
    private val elementBtnLogin: FrameLayout
    private val btnLoginMvcView: ProgressButtonMvcView
    private val btnRegister: Button
    private val btnForgotPassword: Button
    private val edtEmail: EditText
    private val edtPassword: EditText

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?) {
        val rootView = layoutInflater.inflate(R.layout.layout_login, container, false)
        setRootView(rootView)
        elementBtnLogin = findViewById(R.id.elementBtnLogin)
        btnRegister = findViewById(R.id.btnSignUp)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLoginMvcView = ProgressButtonMvcView(layoutInflater, elementBtnLogin, "Login")
        elementBtnLogin.addView(btnLoginMvcView.getRootView())
        btnLoginMvcView.deactivate()
        addEvents()
    }

    private fun addEvents() {
        edtEmail.doOnTextChanged { text, start, before, count ->
            makeSureInputValid()
        }

        edtPassword.doOnTextChanged { text, start, before, count ->
            makeSureInputValid()
        }

        btnForgotPassword.setOnClickListener {
            getListener().forEach {
                it.onBtnForgotPasswordClicked()
            }
        }

        btnRegister.setOnClickListener {
            getListener().forEach {
                it.onBtnRegisterClicked()
            }
        }

        btnLoginMvcView.register(object : ProgressButtonMvcView.Listener {
            override fun onBtnClicked() {
               getListener().forEach {
                   it.onBtnLoginClicked(edtEmail.text.toString(), edtPassword.text.toString())
               }
            }
        })
    }

    private fun makeSureInputValid () {
        mainScope.launch {
            if (checkInput()) {
                btnLoginMvcView.activate()
            } else {
                btnLoginMvcView.deactivate()
            }
            normalLog("check input")
        }
    }

    private suspend fun checkInput () : Boolean = withContext(BackgroundDispatcher) {
        edtEmail.text.toString().isEmail() && edtPassword.text.isNotBlank()
    }

    override fun loading() {
        btnLoginMvcView.startProgress("Please wait...", canClick = false)
        setEnableForAll(false)
    }

    override fun loginFailed(errorMessage: String) {
        setEnableForAll(true)
        makeSureInputValid()
        btnLoginMvcView.stopProgress(canClick = true)
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun loginSuccess() {
        normalLog("Finish")
        btnLoginMvcView.finishProgress("WELCOME", canClick = false)
        setEnableForAll(false)
    }

    private fun setEnableForAll (isEnable : Boolean) {
        btnRegister.isEnabled = isEnable
        btnForgotPassword.isEnabled = isEnable
        edtPassword.isEnabled = isEnable
        edtEmail.isEnabled = isEnable
    }
}