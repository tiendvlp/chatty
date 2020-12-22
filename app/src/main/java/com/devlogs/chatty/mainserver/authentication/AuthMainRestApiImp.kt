package com.devlogs.chatty.mainserver.authentication

import android.util.Log
import com.devlogs.chatty.mainserver.common.restconfig.AuthMainRestClientConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class AuthMainRestApiImp : AuthMainRestApi {
    private val mAuthRetrofit : Retrofit

    @Inject
    constructor( authRetrofit: Retrofit) {
        mAuthRetrofit = authRetrofit
    }

    override fun loginByEmail(
        email: String,
        password: String,
    ): Single<AuthMainRestClientConfig.LoginByEmailResBody> {
        return Single.create { emitter ->
            val authClientConfig = mAuthRetrofit.create(AuthMainRestClientConfig::class.java)
            val reqBody : AuthMainRestClientConfig.LoginByEmailReqBody = AuthMainRestClientConfig.LoginByEmailReqBody(email, password)

            authClientConfig.loginByEmail(reqBody).enqueue(object : Callback<AuthMainRestClientConfig.LoginByEmailResBody> {
                override fun onResponse(
                    call: Call<AuthMainRestClientConfig.LoginByEmailResBody>,
                    response: Response<AuthMainRestClientConfig.LoginByEmailResBody>,
                ) {
                    if (response.code() == 200) {
                        Log.d("AuthMainRestApiImp", "Login Success")
                        emitter.onSuccess(response.body()!!)
                        return
                    }

                    Log.d("AuthMainRestApiImp", "Login failed" + "upper")
                    emitter.onError(Error(response.message()))

                }

                override fun onFailure(
                    call: Call<AuthMainRestClientConfig.LoginByEmailResBody>,
                    t: Throwable,
                ) {
                    Log.d("AuthMainRestApiImp", "Login failed: " + t.message)
                    emitter.onError(t)
                }

            })
        }
    }

    override fun register(email: String, password: String): Completable {
        return Completable.create { emitter ->
            val authClientConfig = mAuthRetrofit.create(AuthMainRestClientConfig::class.java)
            val reqBody : AuthMainRestClientConfig.RegisterReqBody = AuthMainRestClientConfig.RegisterReqBody(email, password)

            authClientConfig.register(reqBody).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.code() == 200) {
                        Log.d("AuthMainRestApiImp", "Register success")
                        emitter.onComplete()
                        return
                    }

                    Log.d("AuthMainRestApiImp", "Register failed")
                    emitter.onError(Error("Register failed"))
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("AuthMainRestApiImp", "Register failed")
                    emitter.onError(t)
                }

            })
        }
    }
}