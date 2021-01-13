package com.devlogs.chatty.datasource.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class LogcatInterceptor : Interceptor {

    @Inject
    constructor() {

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()

        Log.i("Request", String.format("Sending request %s \n HEADER: %s",
                request.url(), request.headers()))

        val response = chain.proceed(chain.request())

        val t2 = System.nanoTime()
        Log.i("Response", java.lang.String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers()))

        return response
    }
}