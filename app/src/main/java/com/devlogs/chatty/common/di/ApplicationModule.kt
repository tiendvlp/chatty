package com.devlogs.chatty.common.di

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.apollographql.apollo.ApolloClient
import com.devlogs.chatty.common.AUTH_SERVER_START_PATH
import com.devlogs.chatty.common.HOST
import com.devlogs.chatty.common.MAIN_SERVER_START_PATH
import com.devlogs.chatty.common.application.ApplicationEventObservable
import com.devlogs.chatty.datasource.common.http_interceptor.AuthInterceptor
import com.devlogs.chatty.datasource.common.http_interceptor.LogcatInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.realm.RealmConfiguration
import io.socket.client.IO
import io.socket.client.Socket
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
                .baseUrl(AUTH_SERVER_START_PATH)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build()
    }

    @Provides
    @Singleton
    @Named(DaggerNamed.Retrofit.MainServerRetrofit)
    fun provideMainServerRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(MAIN_SERVER_START_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    @Provides
    @Singleton
    fun provideApplicationEventObservable () : ApplicationEventObservable {
        return ApplicationEventObservable()
    }

    @Provides
    @Singleton
    fun provideRealmInstance () : RealmConfiguration {
        return RealmConfiguration.Builder()
            .name("CHATTY_LOCAL_DB")
            .schemaVersion(1)
            .allowQueriesOnUiThread(false)
            .allowWritesOnUiThread(false)
            .build()

    }

    @Provides
    @Singleton
    fun provideSocketInstance () : Socket  {
        val socketOption = IO.Options()
        socketOption.reconnection = true
        socketOption.reconnectionDelay = 5000
        socketOption.forceNew = true
        return IO.socket("http://$HOST:3000", socketOption)
    }

    @Provides
    @Singleton
    fun provideApolloClient(client: OkHttpClient): ApolloClient {

        return ApolloClient.builder()
                .serverUrl("http://$HOST:3000/graphql")
                .okHttpClient(client)
                .build()

    }


}