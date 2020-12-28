package com.devlogs.chatty.common.di

class DaggerNamed {
    object ErrorHandler {
        const val GeneralErrorHandler = "GeneralErrorHandler"
    }
    object Retrofit {
        const val AuthServerRetrofit = "AuthServerRetrofit"
        const val MainServerRetrofit = "MainServerRetrofit"
    }
}