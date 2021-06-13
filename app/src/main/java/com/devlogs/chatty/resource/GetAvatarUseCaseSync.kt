package com.devlogs.chatty.resource

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.getFullDownloadAvatarUrl
import com.devlogs.chatty.common.helper.getImageBytes
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetAvatarUseCaseSync  @Inject constructor (private val internalResource: InternalResource) {
    sealed class Result {
        data class Success (val bytes : ByteArray) : Result ()
        object  DuplicateError : Result ()
        object GeneralError : Result()
    }

    suspend fun execute (userEmail: String) : Result = withContext(BackgroundDispatcher) {
        val avatarFileName = getAvatarFileName(userEmail)
        try {
            normalLog("ResourceFound")
            Result.Success(internalResource.read(avatarFileName))
        } catch (exception: CommonErrorEntity.ResourceNotFoundErrorEntity) {
            normalLog("Resource not found start download from internet")
            val resource = getImageBytes(getFullDownloadAvatarUrl(userEmail))
            CoroutineScope(BackgroundDispatcher).launch {
                normalLog("Download finished start saving")
                internalResource.write(avatarFileName, true, resource)
            }
            Result.Success(resource)
        } catch (exception: IOException) {
            Result.GeneralError
        }
    }

    private fun getAvatarFileName(userEmail: String): String {
        return "${userEmail}_avatar"
    }
}