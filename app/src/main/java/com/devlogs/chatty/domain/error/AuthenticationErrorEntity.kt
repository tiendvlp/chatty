package com.devlogs.chatty.domain.error

sealed class AuthenticationErrorEntity(override val message: String) : ErrorEntity(message) {
    class InvalidRefreshTokenErrorEntity () : AuthenticationErrorEntity ("Your refresh token is expired or invalid, login to get a new one")
    class InvalidAccessTokenErrorEntity () : AuthenticationErrorEntity ("Your access token is expired or invalid")
    class CannotGetAccessTokenErrorEntity (override val message: String) : AuthenticationErrorEntity(message)
    class CannotGetRefreshTokenErrorEntity (override val message: String) : AuthenticationErrorEntity(message)
    class GeneralErrorEntity (override val message: String) : AuthenticationErrorEntity (message)
}