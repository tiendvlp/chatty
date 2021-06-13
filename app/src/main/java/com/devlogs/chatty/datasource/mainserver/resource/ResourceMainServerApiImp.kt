package com.devlogs.chatty.datasource.mainserver.resource

import com.devlogs.chatty.common.getFullDownloadAvatarUrl
import com.devlogs.chatty.common.getFullResourceDownloadUrl
import com.devlogs.chatty.domain.datasource.mainserver.ResourceMainServer
import java.io.File


class ResourceMainServerApiImp : ResourceMainServer {
    override fun getResourceUrl(fileName: String): String {
        return getFullResourceDownloadUrl(fileName)
    }

    override fun getAvatarUrl(userEmail: String): String {
        return getFullDownloadAvatarUrl(userEmail)
    }
}