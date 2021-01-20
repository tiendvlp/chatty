package com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.devlogs.chatty.R
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.isEmail
import com.devlogs.chatty.common.helper.isValidPassword
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterMvcViewImp : BaseMvcView<RegisterMvcView.Listener>, RegisterMvcView {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtRetypePassword: EditText
    private lateinit var btnNext: Button
    private lateinit var btnSignIn: Button

    private var mainCoroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?) {
        setRootView(layoutInflater.inflate(R.layout.layout_register, container, false))
        addControls()
        addEvents()
    }

    private fun addControls () {
        edtEmail = findViewById(R.id.edtEmail)
        edtName = findViewById(R.id.edtName)
        edtPassword = findViewById(R.id.edtPassword)
        edtRetypePassword = findViewById(R.id.edtRetypePassword)
        btnNext = findViewById(R.id.btnNext)
        btnSignIn = findViewById(R.id.btnSignIn)
    }

    private fun addEvents () {
        btnNext.setOnClickListener {
            getListener().forEach {
                it.onBtnRegisterClicked()
            }
        }

        btnSignIn.setOnClickListener {
            getListener().forEach {
                it.onBtnSignInClicked()
            }
        }

        edtEmail.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) { return@setOnFocusChangeListener }
            mainCoroutineScope.launch {
                if (checkEmail(edtEmail.text.toString())) {
                    Toast.makeText(getContext(), "Your email is invalid", Toast.LENGTH_SHORT).show()
                } else {

                }
            }
        }
    }

    override fun showLoadingLayout() {
        TODO("Not yet implemented")
    }

    override fun hideLoadingLayout() {
        TODO("Not yet implemented")
    }


    // to make sure the UI is not handle too much
    private suspend fun checkEmail (target: String) : Boolean = withContext(BackgroundDispatcher) { target.isEmail() }
    private suspend fun checkPassword (target: String) : Boolean = withContext(BackgroundDispatcher) { target.isValidPassword() }

}