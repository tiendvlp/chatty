package com.devlogs.chatty.datasource.errorhandler

import com.devlogs.chatty.domain.error.ErrorEntity
import com.devlogs.chatty.domain.error.ErrorHandler
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class GeneralErrorHandlerImp : ErrorHandler {
    override fun getError(t: Throwable): ErrorEntity {
        when (t) {
            is IOException -> return ErrorEntity.NetworkError
            is HttpException -> return getErrorFromHttpCode(t.code())
        }
        return ErrorEntity.UnknownError
    }

    private fun getErrorFromHttpCode (code: Int) : ErrorEntity {
        when (code) {
            HttpURLConnection.HTTP_NOT_FOUND -> return ErrorEntity.NotFoundError
            400 -> return ErrorEntity.UnAuthorizedError
        }

        return ErrorEntity.UnknownError
    }
}