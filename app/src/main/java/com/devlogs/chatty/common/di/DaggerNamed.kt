package com.devlogs.chatty.common.di

import com.devlogs.chatty.datasource.interceptor.AuthInterceptor

class DaggerNamed {
    object Retrofit {
        const val AuthServerRetrofit = "AuthServerRetrofit"
        const val MainServerRetrofit = "MainServerRetrofit"
    }
    object Interceptor {
        const val AuthInterceptor = "AuthInterceptor"
        const val LoggingInterceptor = "LoggingInterceptor"
    }

}