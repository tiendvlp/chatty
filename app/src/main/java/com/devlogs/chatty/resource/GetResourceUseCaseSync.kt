package com.devlogs.chatty.resource

import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.getFullResourceDownloadUrl
import com.devlogs.chatty.common.helper.getImageBytes
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetResourceUseCaseSync @Inject constructor(private val internalResource: InternalResource) {

    sealed class Result {
        data class Success (val bytes : ByteArray) : Result ()
        object  DuplicateError : Result ()
        object GeneralError : Result()
    }

    suspend fun execute (fileName: String) : Result = withContext(BackgroundDispatcher) {
        try {
            Result.Success(internalResource.read(fileName))
        } catch (exception: CommonErrorEntity.ResourceNotFoundErrorEntity) {
            val resource = getImageBytes(getFullResourceDownloadUrl(fileName))
            CoroutineScope(BackgroundDispatcher).launch {
                internalResource.write(fileName, true, resource)
            }
            Result.Success(resource)
        } catch (exception: IOException) {
            Result.GeneralError
        }
    }
}