package com.devlogs.chatty.domain.error

open class ErrorEntity : Throwable {
    constructor(message: String) : super(message)
}


