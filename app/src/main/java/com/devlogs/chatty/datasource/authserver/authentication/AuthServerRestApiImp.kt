package com.devlogs.chatty.datasource.authserver.authentication

import android.util.Log
import com.devlogs.chatty.common.Either
import com.devlogs.chatty.common.None
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.restconfig.AuthServerRestClientConfig
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.LoginMainServerModel
import com.devlogs.chatty.domain.error.ErrorEntity
import com.devlogs.chatty.domain.error.ErrorHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class AuthServerRestApiImp : AuthServerApi {

    private val mAuthRetrofit: Retrofit
    private val mErrorHandler: ErrorHandler
    private val mAuthClientConfig : AuthServerRestClientConfig

    @Inject
    constructor(@Named(DaggerNamed.Retrofit.AuthServerRetrofit) authRetrofit: Retrofit, @Named(DaggerNamed.ErrorHandler.GeneralErrorHandler) errorHandler: ErrorHandler) {
        mAuthRetrofit = authRetrofit
        mErrorHandler = errorHandler
        mAuthClientConfig = mAuthRetrofit.create(AuthServerRestClientConfig::class.java)
    }

    override suspend fun loginByEmail(email: String, password: String): Either<ErrorEntity, LoginMainServerModel> = withContext(Dispatchers.IO) {
        val reqBody: AuthServerRestClientConfig.LoginByEmailReqBody = AuthServerRestClientConfig.LoginByEmailReqBody(email, password)
        val loginResponse = mAuthClientConfig.loginByEmail(reqBody)

        if (loginResponse.code() == 200) {
            Log.d("AuthMainRestApiImp", "Login Success")
            return@withContext Either.Right(LoginMainServerModel(loginResponse.body()!!.accessToken, loginResponse.body()!!.refreshToken))
        }
        // if the request send failed
        if (!loginResponse.isSuccessful) {
            return@withContext Either.Left(ErrorEntity.ConnectionError)
        }
        if (loginResponse.code() == 400) {
            return@withContext Either.Left(ErrorEntity.AuthError.InvalidAccountError)

        }
        return@withContext Either.Left(ErrorEntity.UnknownError)
    }


    override suspend fun register(email: String, password: String): Either<ErrorEntity, None> = withContext(Dispatchers.IO) {
        val reqBody: AuthServerRestClientConfig.RegisterReqBody = AuthServerRestClientConfig.RegisterReqBody(email, password)

        try {
            /*
            * This line of code can cause the cancellation exception
            * */
            val registerResponse = mAuthClientConfig.register(reqBody)

            if (registerResponse.code() == 200) {
                return@withContext Either.Right(None)
            }
            return@withContext Either.Left(mErrorHandler.getError(HttpException(registerResponse)))

        }
        catch (e : Exception) {
            normalLog("Send login request failed" + e.message)
            return@withContext Either.Left(mErrorHandler.getError(e))
        }
    }

    override suspend fun generateNewAccessToken(refreshToken: String): String = withContext(Dispatchers.IO) {
        try {
            val generateAccessTokenResponse = mAuthClientConfig.generateNewAccessToken(AuthServerRestClientConfig.GenerateNewAccessTokenReq.ReqBody(refreshToken))
            if (generateAccessTokenResponse.code() == 200) {
                return@withContext generateAccessTokenResponse.body()!!.accessToken
            }
            throw ErrorEntity.UnknownError
        } catch (e: HttpException) {
            throw mErrorHandler.getError(e)
        }
    }

}