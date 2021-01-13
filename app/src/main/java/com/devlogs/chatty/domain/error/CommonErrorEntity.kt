package com.devlogs.chatty.domain.error

open class CommonErrorEntity(message: String) : ErrorEntity (message) {
    class NetworkErrorEntity(message: String) : CommonErrorEntity (message)
    class IOErrorEntity (message: String) : CommonErrorEntity (message)
    class TimeOutErrorEntity (message: String) : CommonErrorEntity (message)
    class UnavailableErrorEntity (message: String) : CommonErrorEntity (message)
    class UnAuthorizedErrorEntity (message: String) : CommonErrorEntity (message)
    class InvalidRequestErrorEntity (message: String) : CommonErrorEntity (message)
    class NotFoundErrorEntity (message: String) : CommonErrorEntity (message)
    class GeneralErrorEntity (message: String) : CommonErrorEntity (message)
    class TokenExpiredErrorEntity (message: String) : CommonErrorEntity(message)
    class DuplicateErrorEntity (message: String) : CommonErrorEntity(message)
    class UnSupportedDataType (message: String) : CommonErrorEntity(message)
}