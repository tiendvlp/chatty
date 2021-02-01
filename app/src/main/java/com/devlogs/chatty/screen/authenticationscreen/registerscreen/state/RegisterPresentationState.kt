package com.devlogs.chatty.screen.authenticationscreen.registerscreen.state

import com.devlogs.chatty.screen.authenticationscreen.registerscreen.state.RegisterPresentationAction.*
import com.devlogs.chatty.screen.common.presentationstate.InvalidActionException
import com.devlogs.chatty.screen.common.presentationstate.PresentationAction
import com.devlogs.chatty.screen.common.presentationstate.PresentationState

sealed class RegisterPresentationState : PresentationState{
//    override fun getTag(): String {
//        return javaClass.simpleName
//    }
//
//    open var email: String
//    var password: String
//    var userName: String
//
//    constructor(email: String, password: String, userName: String) {
//        this.email = email
//        this.password = password
//        this.userName = userName
//    }
//
//
//    data class NotRegisterState(override var email: String) : RegisterPresentationState() {
//        override fun consumeAction(action: PresentationAction): PresentationState {
//            when (action) {
//                is RegisterAction -> return LoadingRegisterState(action.email, action.password)
//            }
//            throw InvalidActionException("${getTag()}.NotRegisterState", action.toString())
//        }
//    }
//
//    data class LoadingRegisterState (val email: String, val password: String): RegisterPresentationState() {
//        override fun consumeAction(action: PresentationAction): PresentationState {
//            when (action) {
//                is RegisterFailedAction -> return NotRegisterState
//                is RegisterSuccessAction -> return RegisterSuccessState
//            }
//            throw InvalidActionException("${getTag()}.LoadingState", action.toString())
//        }
//    }
//
//    object RegisterSuccessState : RegisterPresentationState() {
//        override fun consumeAction(action: PresentationAction): PresentationState {
//            throw InvalidActionException("${getTag()}.RegisterSuccessState", action.toString())
//        }
//    }
//
//    data class LoginState() : RegisterPresentationState () {
//        override fun consumeAction(action: PresentationAction): PresentationState {
//            TODO("Not yet implemented")
//        }
//    }
//
//    data class LoginSuccessState () : RegisterPresentationState () {
//        override fun consumeAction(action: PresentationAction): PresentationState {
//
//        }
//    }
//
//    data class CreateUserState () : RegisterPresentationState () {
//
//    }
}