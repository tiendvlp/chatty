package com.devlogs.chatty.resource

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.getFullDownloadAvatarUrl
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.error.CommonErrorEntity.ResourceNotFoundErrorEntity
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject


class GetUserAvatarUrlUseCaseSync @Inject constructor (private val internalResource: InternalResource, private val cachingResourceUseCase: GetResourceUseCaseSync) {
    sealed class Result {
        data class Success(val type: UrlType, val url: String) : Result()
        data class GeneralError(val exception: Exception) : Result()
    }

    suspend fun execute(userEmail: String): Result = withContext(BackgroundDispatcher) {
        val fileName = getAvatarFileName(userEmail)
        try {
            Result.Success(type = UrlType.LOCAL, url = internalResource.getUrl(fileName))
        } catch (exception: ResourceNotFoundErrorEntity) {
            Result.Success(type = UrlType.HTTP, url = getFullDownloadAvatarUrl(userEmail))
        } catch (exception: IOException) {
            Result.GeneralError(exception)
        }
    }

    private fun getAvatarFileName(userEmail: String): String {
        return "${userEmail}_avatar"
    }
}