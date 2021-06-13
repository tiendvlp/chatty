package com.devlogs.chatty.user

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.errorLog
import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.datasource.local.process.AccountLocalDbApi
import com.devlogs.chatty.datasource.local.relam_object.toAccountEntity
import com.devlogs.chatty.datasource.local.relam_object.toAccountRO
import com.devlogs.chatty.domain.datasource.mainserver.UserMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.toAccountEntity
import com.devlogs.chatty.domain.entity.AccountEntity
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import com.devlogs.chatty.user.GetAccountUseCase.Result.Success
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAccountUseCase {
    sealed class Result {
        data class Success(val accountEntity: AccountEntity) : Result()
        object NetworkError : Result()
        object GeneralError : Result ()
    }

    private val mUserMainServerApi : UserMainServerApi
    private val accountLocalDbApi : AccountLocalDbApi

    @Inject
    constructor(userMainServerApi: UserMainServerApi, accountLocalDbApi: AccountLocalDbApi) {
        mUserMainServerApi = userMainServerApi
        this.accountLocalDbApi = accountLocalDbApi
    }

    suspend fun execute () : Result = withContext(BackgroundDispatcher) {
        try {
            var result : AccountEntity? = accountLocalDbApi.getAccount()?.let {
                toAccountEntity(
                        it
                )
            }
            if (result == null) {
                result = Mapper().toAccountEntity(mUserMainServerApi.getMyAccount())
            }
            launch {
                accountLocalDbApi.setAccount(toAccountRO(result))
            }
            Success(result)
        } catch (e: GeneralErrorEntity) {
            errorLog("Get user error: General error")
            Result.GeneralError
        } catch (e: NetworkErrorEntity) {
            errorLog("Get user error: Network error")
            Result.NetworkError
        }
    }

}