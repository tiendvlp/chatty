package com.devlogs.chatty.authentication.accesstoken

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAccessTokenUseCaseSync {

    sealed class Result {
        data class Success (val accessToken : String) : Result()
        object GeneralError : Result()
    }

    suspend fun execute () : Result = withContext(Dispatchers.IO) {

        return@withContext Result.GeneralError
    }


}