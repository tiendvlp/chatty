package com.devlogs.chatty.common.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.apollographql.apollo.ApolloClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideSharedPreference(appContext: Application) : SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    @Provides
    @Singleton
    @Named(DaggerNamed.Retrofit.AuthServerRetrofit)
    fun provideAuthServerRetrofit () : Retrofit {
        val okHttpClient = OkHttpClient.Builder().callTimeout(21, TimeUnit.SECONDS)
            .connectTimeout(21, TimeUnit.SECONDS).build()

        return Retrofit.Builder() //        http://10.0.2.2:8080/api/v1/
            // 172.20.10.4
            .baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named(DaggerNamed.Retrofit.MainServerRetrofit)
    fun provideMainServerRetrofit () : Retrofit {
        val okHttpClient = OkHttpClient.Builder().callTimeout(21, TimeUnit.SECONDS)
            .connectTimeout(21, TimeUnit.SECONDS).build()

        return Retrofit.Builder() //        http://10.0.2.2:8080/api/v1/
            // 172.20.10.4
            .baseUrl("http://10.0.2.2:3000/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient () : ApolloClient {
        return ApolloClient.builder().serverUrl("http://10.0.2.2:3000/graphql").build()
    }
}