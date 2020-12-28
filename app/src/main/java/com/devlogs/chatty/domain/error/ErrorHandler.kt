package com.devlogs.chatty.domain.error

import com.devlogs.chatty.domain.error.ErrorEntity

interface ErrorHandler {
    fun getError (t : Throwable) : ErrorEntity
}