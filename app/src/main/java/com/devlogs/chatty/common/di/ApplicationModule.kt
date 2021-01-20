package com.devlogs.chatty.common.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.apollographql.apollo.ApolloClient
import com.devlogs.chatty.config.LOCALHOST
import com.devlogs.chatty.datasource.interceptor.AuthInterceptor
import com.devlogs.chatty.datasource.interceptor.LogcatInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideSharedPreference(appContext: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, logcatInterceptor: LogcatInterceptor) =
            OkHttpClient.Builder()
                    .callTimeout(21, TimeUnit.SECONDS)
                    .addInterceptor(logcatInterceptor)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(21, TimeUnit.SECONDS).build()


    @Provides
    @Singleton
    @Named(DaggerNamed.Retrofit.AuthServerRetrofit)
    fun provideAuthServerRetrofit(logcatInterceptor: LogcatInterceptor): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
                .callTimeout(21, TimeUnit.SECONDS)
                .addInterceptor(logcatInterceptor)
                .connectTimeout(21, TimeUnit.SECONDS).build()

        return Retrofit.Builder()
                // 172.20.10.4

                .baseUrl("http://$LOCALHOST:4000")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build()
    }


    @Provides
    @Singleton
    @Named(DaggerNamed.Retrofit.MainServerRetrofit)
    fun provideMainServerRetrofit(client: OkHttpClient): Retrofit {

        return Retrofit.Builder()
                //http://10.0.2.2:8080/api/v1/
                // 172.20.10.4
                .baseUrl("http://$LOCALHOST:3000/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(client: OkHttpClient): ApolloClient {
        return ApolloClient.builder()
                .serverUrl("http://$LOCALHOST:3000/graphql")
                .okHttpClient(client)
                .build()

    }

}