import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import javax.inject.Inject
import com.devlogs.chatty.common.getFullResourceDownloadUrl
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.error.CommonErrorEntity.ResourceNotFoundErrorEntity
import com.devlogs.chatty.resource.UrlType
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

class GetResourceUrlUseCaseSync @Inject constructor (private val internalResource: InternalResource) {
    sealed class Result {
        data class Success(val type: UrlType, val url: String) : Result()
        data class GeneralError(val exception: Exception) : Result()
    }

    suspend fun execute(fileName: String): Result = withContext(BackgroundDispatcher) {
        try {
            Result.Success(type = UrlType.LOCAL, url = internalResource.getUrl(fileName))
        } catch (exception: ResourceNotFoundErrorEntity) {
            Result.Success(type = UrlType.HTTP, url = getFullResourceDownloadUrl(fileName))
        } catch (exception: IOException) {
            Result.GeneralError(exception)
        }
    }
}