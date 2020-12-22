package com.devlogs.chatty.common.di

import com.devlogs.chatty.mainserver.authentication.AuthMainRestApi
import com.devlogs.chatty.mainserver.authentication.AuthMainRestApiImp
import com.devlogs.chatty.repository.authentication.TokenRepository
import com.devlogs.chatty.repository.authentication.TokenRepositoryImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
class DataDiModule {

    @Provides
    fun provideAuthMainResApi (@Named ("AuthServerRetrofit") retrofit: Retrofit) : AuthMainRestApi {
        return AuthMainRestApiImp(retrofit)
    }

    @Provides
    fun provideTokenRepository () : TokenRepository {
        return TokenRepositoryImp()
    }
}