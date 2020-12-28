package com.devlogs.chatty.common.di

import com.devlogs.chatty.datasource.errorhandler.GeneralErrorHandlerImp
import com.devlogs.chatty.datasource.authserver.authentication.AuthServerRestApiImp
import com.devlogs.chatty.datasource.mainserver.user.UserMainGraphqlApiImp
import com.devlogs.chatty.domain.datasource.authserver.AuthServerApi
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.error.ErrorHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Named

@Module
@InstallIn(ApplicationComponent::class)
class DataDiModule {

    @Provides
    @Named(DaggerNamed.ErrorHandler.GeneralErrorHandler)
    fun provideGeneralErrorHandler () : ErrorHandler {
        return GeneralErrorHandlerImp()
    }

    @Provides
    fun provideAuthMainRestApi (authMainRestApiImp: AuthServerRestApiImp) : AuthServerApi {
        return authMainRestApiImp
    }

    @Provides
    fun provideUserMainServerGraphqlApi (userMainGraphqlApiImp: UserMainGraphqlApiImp) : UserMainServerApi {
        return userMainGraphqlApiImp
    }

}