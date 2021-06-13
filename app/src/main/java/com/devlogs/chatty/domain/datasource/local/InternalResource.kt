package com.devlogs.chatty.domain.datasource.local

import com.devlogs.chatty.domain.error.CommonErrorEntity
import kotlin.Throws

interface InternalResource {

    /**
    * @throws  CommonErrorEntity.DuplicateErrorEntity if resource already exist and override = false
    * @throws CommonErrorEntity.IOErrorEntity
    **/
    @Throws(CommonErrorEntity.DuplicateErrorEntity::class, CommonErrorEntity.IOErrorEntity::class)
     fun write (fileName: String, override: Boolean, content: ByteArray) : String
    /**
    * @throws CommonErrorEntity.ResourceNotFoundErrorEntity
     * @throws CommonErrorEntity.IOErrorEntity
    */
    @Throws(CommonErrorEntity.ResourceNotFoundErrorEntity::class, CommonErrorEntity.IOErrorEntity::class)
     fun read (fileName: String) : ByteArray

    /**
     * @throws CommonErrorEntity.ResourceNotFoundErrorEntity
     */
    @Throws(CommonErrorEntity.ResourceNotFoundErrorEntity::class)
     fun getUrl (fileName: String) : String


}