package com.devlogs.chatty.domain.error

sealed class ErrorEntity : Throwable {
    constructor() : super()
    constructor(message: String) : super(message)
    object NetworkError  : ErrorEntity()
    object IOError : ErrorEntity()
    object TimeOutError : ErrorEntity()
    object UnknownError : ErrorEntity()
    object ConnectionError : ErrorEntity()
    object UnavailableError : ErrorEntity()
    object UnAuthorizedError : ErrorEntity()
    object InvalidRequestError : ErrorEntity()
    object UnSupportedDataTypeError : ErrorEntity()
    object NotFoundError : ErrorEntity()
    object MissingDataError : ErrorEntity()

    sealed class AuthError : ErrorEntity () {
        object InvalidAccountError : AuthError()
        object AccountAlreadyExistError : AuthError()
    }

}