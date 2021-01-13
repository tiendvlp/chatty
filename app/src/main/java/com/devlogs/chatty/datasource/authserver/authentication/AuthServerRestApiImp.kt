package com.devlogs.chatty.datasource.authserver.authentication

import android.util.Log
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.datasource.common.restconfig.AuthServerRestClientConfig
import com.devlogs.chatty.datasource.common.restconfig.AuthServerRestClientConfig.LoginByEmailReqBody
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi.LoginByEmailResult
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi.LoginByEmailResult.*
import com.devlogs.chatty.domain.error.CommonErrorEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class AuthServerRestApiImp : AuthServerApi {
    private val mAuthRetrofit: Retrofit
    private val mAuthClientConfig : AuthServerRestClientConfig

    @Inject
    constructor(@Named(DaggerNamed.Retrofit.AuthServerRetrofit) authRetrofit: Retrofit) {
        mAuthRetrofit = authRetrofit
        mAuthClientConfig = mAuthRetrofit.create(AuthServerRestClientConfig::class.java)
    }

    override suspend fun loginByEmail(email: String, password: String): LoginByEmailResult {
        val reqBody = LoginByEmailReqBody(email, password)
        try {
            Log.d("AuthMainRestApiImp", "Login in progress")
            val loginResponse = mAuthClientConfig.loginByEmail(reqBody)
            Log.d("AuthMainRestApiImp", "Login in progress")

            if (loginResponse.code() == 200) {
                Log.d("AuthMainRestApiImp", "Login Success")

                val resBody = loginResponse.body() ?:  throw GeneralErrorEntity("The loginByEmail ReqBody is missing")

                val accessTokenRes = Token(resBody.accessToken.token, resBody.accessToken.expiredAt)
                val refreshTokenRes = Token(resBody.refreshToken.token, resBody.refreshToken.expiredAt)
                return LoginByEmailResult(accessTokenRes, refreshTokenRes)
            }
            if (loginResponse.code() == 400) {
                throw NotFoundErrorEntity("Your account with id: $email doesn't exist")
            }
            throw GeneralErrorEntity("")
        } catch (e: HttpException) {
            throw NetworkErrorEntity(e.message())
        }
    }

    override suspend fun register(email: String, password: String) {
        val reqBody: AuthServerRestClientConfig.RegisterReqBody = AuthServerRestClientConfig.RegisterReqBody(email, password)

        try {
            /*
            * This line of code can cause the cancellation exception
            * */
            val registerResponse = mAuthClientConfig.register(reqBody)


            if (!registerResponse.isSuccessful) {

                if (registerResponse.code() == 409) {
                    throw DuplicateErrorEntity("Your account with id: $email already exist")
                }

                throw GeneralErrorEntity("register error code: " + registerResponse.code())
            }
        }
        catch (e : HttpException) {
            throw NetworkErrorEntity(e.message())
        }
    }
}