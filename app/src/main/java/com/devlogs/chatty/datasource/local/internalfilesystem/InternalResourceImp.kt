package com.devlogs.chatty.datasource.local.internalfilesystem

import android.util.Log
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.common.helper.warningLog
import com.devlogs.chatty.domain.datasource.local.InternalResource
import com.devlogs.chatty.domain.error.CommonErrorEntity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class InternalResourceImp @Inject constructor (@Named(DaggerNamed.File.ExternalFileDir) private val externalStorageDir: File) : InternalResource {

    private fun getPath (fileName: String) : String {
        return "${externalStorageDir.path}/resource/$fileName"
    }

    override fun write(fileName: String, override: Boolean, content: ByteArray): String {
        writeToFile(getPath(fileName), override, content)
        return getPath(fileName)
    }

    override fun read(fileName: String): ByteArray {
        val file = File(getPath(fileName))
        if (!file.exists()) throw CommonErrorEntity.ResourceNotFoundErrorEntity("Couldn't find $fileName in resource folder")
        try {
            val fos = FileInputStream(file)
            return fos.readBytes()
        } catch (exception: IOException) {
            throw CommonErrorEntity.IOErrorEntity(exception.message!!)
        }
    }

    override fun getUrl(fileName: String): String {
        val file = File(externalStorageDir.path + "/resource/$fileName")
        if (file.exists()) {
            return file.path
        }
        throw CommonErrorEntity.ResourceNotFoundErrorEntity("Couldn't find resource ${file.path}")
    }

    private fun writeToFile (filePath: String, override: Boolean, bytes: ByteArray) {
        val file = File(filePath)
        if (!override && file.exists()) throw CommonErrorEntity.DuplicateErrorEntity("File $filePath already exist")

        if (file.exists()) {
            file.delete()
        }

        file.parentFile.mkdirs()
        Log.d("FileSystemPath", file.path)
        try {
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.close()
            warningLog(file.path)
        } catch (exception: IOException) {
            throw CommonErrorEntity.IOException(exception.message!!)
        }
    }
}