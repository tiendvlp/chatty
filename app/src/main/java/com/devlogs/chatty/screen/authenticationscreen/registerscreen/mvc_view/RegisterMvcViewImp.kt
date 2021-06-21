package com.devlogs.chatty.screen.authenticationscreen.registerscreen.mvc_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import com.devlogs.chatty.R
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.isEmail
import com.devlogs.chatty.common.helper.isValidPassword
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.CheckingInputMvcView
import com.devlogs.chatty.screen.common.mvcview.ProgressButtonMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import kotlinx.coroutines.*

class RegisterMvcViewImp : BaseMvcView<RegisterMvcView.Listener>, RegisterMvcView {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtRetypePassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var checkingEmailMvcView: CheckingInputMvcView
    private lateinit var checkingPasswordMvcView: CheckingInputMvcView
    private lateinit var btnRegisterMvcView: ProgressButtonMvcView

    private var mainCoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var checkingEmailJob: Job? = null
    private var checkingPasswordJob: Job? = null
    private var isEmailValid = false
        set(value) {
            field = value
            preventInvalidInput()
        }
    private var isPasswordValid = false
        set(value) {
            field = value
            preventInvalidInput()
        }
    private var isConfirmPasswordMatch = false
        set(value) {
            field = value
            preventInvalidInput()
        }
    private var isNameValid = false
        set(value) {
            field = value
            preventInvalidInput()
        }

    constructor(uiToolkit: UIToolkit, container: ViewGroup?) {
        setRootView(uiToolkit.layoutInflater.inflate(R.layout.layout_register, container, false))
        findViewsById()
        addSubMvcView(uiToolkit.layoutInflater)
        addEvents()

    }

    private fun addSubMvcView (layoutInflater: LayoutInflater) {
        val checkEmailElement: FrameLayout = findViewById(R.id.viewCheckingEmail)
        checkingEmailMvcView = CheckingInputMvcView(layoutInflater, checkEmailElement, "Checking...")
        checkEmailElement.addView(checkingEmailMvcView.getRootView())
        checkingEmailMvcView.visibility = View.INVISIBLE

        val checkPasswordElement: FrameLayout = findViewById(R.id.viewCheckingPassword)
        checkingPasswordMvcView = CheckingInputMvcView(layoutInflater, checkPasswordElement, "Checking...")
        checkPasswordElement.addView(checkingPasswordMvcView.getRootView())
        checkingPasswordMvcView.visibility = View.INVISIBLE

        val btnRegisterElement: FrameLayout = findViewById(R.id.btnRegister)
        btnRegisterMvcView = ProgressButtonMvcView(layoutInflater, btnRegisterElement, "Register");
        btnRegisterElement.addView(btnRegisterMvcView.getRootView())
    }

    private fun findViewsById() {
        edtEmail = findViewById(R.id.edtEmail)
        edtName = findViewById(R.id.edtName)
        edtPassword = findViewById(R.id.edtPassword)
        edtRetypePassword = findViewById(R.id.edtRetypePassword)
        btnSignIn = findViewById(R.id.btnSignIn)
    }

    private fun addEvents() {
        btnRegisterMvcView.register(object : ProgressButtonMvcView.Listener{
            override fun onBtnClicked() {
                normalLog("Clickeeeee")
                getListener().forEach {
                    it.onBtnRegisterClicked(edtEmail.text.toString(), edtPassword.text.toString())
                }
            }
        })

        btnSignIn.setOnClickListener {
            getListener().forEach {
                it.onBtnSignInClicked()
            }
        }

        edtName.doOnTextChanged { text, start, before, count ->
            isNameValid = !text.isNullOrBlank()
            normalLog("isNameValid: $isNameValid")
        }

        edtRetypePassword.doOnTextChanged { text, start, before, count ->
            isConfirmPasswordMatch = text.toString().equals(edtPassword.text.toString())
            normalLog("isEdtRetypePasswordValid: $isConfirmPasswordMatch")
        }

        edtEmail.doOnTextChanged { text, start, before, count ->
            checkingEmailJob?.cancel()
            checkingEmailJob = mainCoroutineScope.launch {
                try {
                    checkingEmailMvcView.visibility = View.INVISIBLE
                    delay(2500)
                    checkingEmailMvcView.visibility = View.VISIBLE
                    checkingEmailMvcView.checking("Checking...")

                    isEmailValid = checkEmail(edtEmail.text.toString())
                    if (isEmailValid) {
                        checkingEmailMvcView.valid("Valid")
                    } else {
                        checkingEmailMvcView.inValid("Invalid email")
                    }
                } catch (e: CancellationException) {
                    checkingEmailMvcView.visibility = View.INVISIBLE
                }
            }
        }

        edtPassword.doOnTextChanged { text, start, before, count ->
            checkingPasswordJob?.cancel()
            checkingPasswordJob = mainCoroutineScope.launch {
                try {
                    checkingPasswordMvcView.visibility = View.INVISIBLE
                    delay(2500)
                    checkingPasswordMvcView.visibility = View.VISIBLE
                    checkingPasswordMvcView.checking("Checking...")

                    isPasswordValid = checkPassword(edtPassword.text.toString())
                    if (isPasswordValid) {
                        checkingPasswordMvcView.valid("Valid")
                    } else {
                        checkingPasswordMvcView.inValid("Invalid email")
                    }
                } catch (e: CancellationException) {
                    checkingPasswordMvcView.visibility = View.INVISIBLE
                }
            }
        }

    }

    fun preventInvalidInput() {
        val result = isEmailValid && isPasswordValid && isConfirmPasswordMatch && isNameValid
        normalLog("PreventInvalidInput $result")
        if (result) {
            btnRegisterMvcView.activate()
        } else {
            btnRegisterMvcView.deactivate()
        }
    }

    override fun loading() {
        btnRegisterMvcView.startProgress("Please wait...", canClick = false)
        setEnableForAll(false)
    }

    override fun registerFailed(errMessage: String) {
        setEnableForAll(true)
        preventInvalidInput()
        btnRegisterMvcView.stopProgress(canClick = true)
        Toast.makeText(getContext(), errMessage, Toast.LENGTH_SHORT).show()
    }

    override fun registerSuccess() {
        setEnableForAll(false)
        btnRegisterMvcView.finishProgress("WELCOME", canClick = false)
    }

    private fun setEnableForAll (isEnable: Boolean) {
        btnSignIn.isEnabled = isEnable
        edtName.isEnabled = isEnable
        edtPassword.isEnabled = isEnable
        edtEmail.isEnabled = isEnable
        edtRetypePassword.isEnabled = isEnable
    }

    // to make sure the UI is not handle too much
    private suspend fun checkEmail(target: String): Boolean =
        withContext(BackgroundDispatcher) { delay(1000); target.isEmail() }

    private suspend fun checkPassword(target: String): Boolean =
        withContext(BackgroundDispatcher) { delay(1000); target.isValidPassword() }

}