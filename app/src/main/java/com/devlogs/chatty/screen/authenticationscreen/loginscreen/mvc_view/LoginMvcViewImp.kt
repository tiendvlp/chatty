package com.devlogs.chatty.screen.authenticationscreen.loginscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.isEmail
import com.devlogs.chatty.common.helper.isValidPassword
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginMvcViewImp : BaseMvcView<LoginMvcView.Listener>, LoginMvcView {
    private val mainScope = CoroutineScope(Dispatchers.Main.immediate)
    private val btnLogin : ImageButton
    private val btnRegister : Button
    private val btnForgotPassword : Button
    private val edtEmail : EditText
    private val edtPassword : EditText

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?) {
        val rootView = layoutInflater.inflate(R.layout.layout_login, container, false)
        setRootView(rootView)

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        addEvents()
    }

    private fun addEvents() {
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

        btnLogin.setOnClickListener{
            mainScope.launch {
                if (!checkEmail(edtEmail.text.toString())) {
                    // show warning to user
                    return@launch
                }

                if (!checkPassword(edtPassword.text.toString())) {
                    // show warning to user
                    return@launch
                }

                getListener().forEach {
                    it.onBtnLoginClicked(edtEmail.text.toString(), edtPassword.text.toString())
                }
            }
        }
    }

    // to make sure the UI is not handle too much
    private suspend fun checkEmail (target: String) : Boolean = withContext(Dispatchers.Default) { target.isEmail() }
    private suspend fun checkPassword (target: String) : Boolean = withContext(Dispatchers.Default) { target.isValidPassword() }

}