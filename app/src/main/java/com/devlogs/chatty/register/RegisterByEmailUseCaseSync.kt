package com.devlogs.chatty.register

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterByEmailUseCaseSync {
    sealed class Result {
        object Success : Result()
        object AlreadyExistError : Result ()
        object GeneralError : Result ()
    }

    suspend fun execute () = withContext(Dispatchers.IO) {

    }
}