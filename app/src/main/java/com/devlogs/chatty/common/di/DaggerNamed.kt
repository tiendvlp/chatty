package com.devlogs.chatty.common.di

class DaggerNamed {
    object Retrofit {
        const val AuthServerRetrofit = "AuthServerRetrofit"
        const val MainServerRetrofit = "MainServerRetrofit"
    }
    object Interceptor {
        const val AuthInterceptor = "AuthInterceptor"
        const val LoggingInterceptor = "LoggingInterceptor"
    }
    object File {
        const val ExternalFileDir = "ExternalFileDir"
    }
}