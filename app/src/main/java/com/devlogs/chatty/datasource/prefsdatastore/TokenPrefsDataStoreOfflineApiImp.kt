package com.devlogs.chatty.datasource.prefsdatastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.createDataStore
import com.devlogs.chatty.domain.datasource.offlinedb.TokenOfflineApi
import com.devlogs.chatty.domain.error.ErrorEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

private const val DATASTORE_NAME = "Token_DataStore"

/**
 * @notice the cancellation can occur, i didn't handled it here because the business logic is
 *  + When Access Token is missing -> use refresh token to generate a new one
 *  + When Refresh Token is missing -> Login again -> get a new one
 * @throws ErrorEntity.NotFoundError*/
class TokenPrefsDataStoreOfflineApiImp : TokenOfflineApi {
    private object PreferencesKeys {
        val ACCESS_TOKEN_KEY = preferencesKey<String>("access_token")
        val REFRESH_TOKEN_KEY = preferencesKey<String>("refresh_token")
    }

    private val mDataStore : DataStore<Preferences>

    @Inject
    constructor(@ApplicationContext context: Context) {
        mDataStore = context.createDataStore(name = DATASTORE_NAME)
    }

    override suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        try {
            mDataStore.data.first()[PreferencesKeys.ACCESS_TOKEN_KEY]
                ?: throw ErrorEntity.NotFoundError
        } catch (e: NoSuchElementException) {
            throw ErrorEntity.NotFoundError
        }
    }
    /**
     * @throws ErrorEntity.IOError*/
    override suspend fun setAccessToken(accessToken: String) {
        try {
            mDataStore.edit { tokens ->
                tokens[PreferencesKeys.ACCESS_TOKEN_KEY] = accessToken
            }
        } catch (e : IOException) {
            throw ErrorEntity.IOError
        }
    }

    override suspend fun getRefreshToken(): String = withContext(Dispatchers.IO){
        try {
            mDataStore.data.first()[PreferencesKeys.REFRESH_TOKEN_KEY]
                ?: throw ErrorEntity.NotFoundError
        } catch (e: NoSuchElementException) {
            throw ErrorEntity.NotFoundError
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        try {
            mDataStore.edit { tokens ->
                tokens[PreferencesKeys.REFRESH_TOKEN_KEY] = refreshToken
            }
        } catch (e : IOException) {
            throw ErrorEntity.IOError
        }
    }

    override suspend fun clear() {
        try {
            mDataStore.edit { tokens ->
                tokens.clear()
            }
        } catch (e : IOException) {
            throw ErrorEntity.IOError
        }
    }
}