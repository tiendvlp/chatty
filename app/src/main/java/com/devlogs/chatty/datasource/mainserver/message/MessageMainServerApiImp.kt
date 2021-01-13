package com.devlogs.chatty.datasource.mainserver.message

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.devlogs.chatty.common.di.DaggerNamed
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.datasource.common.helper.simple
import com.devlogs.chatty.datasource.common.restconfig.MessageMainServerRestClientConfig
import com.devlogs.chatty.datasource.common.restconfig.MessageMainServerRestClientConfig.SendTextMessageReqBody
import com.devlogs.chatty.domain.datasource.mainserver.MessageMainServerApi
import com.devlogs.chatty.domain.datasource.mainserver.model.MessageMainServerModel
import com.devlogs.chatty.domain.error.CommonErrorEntity.*
import mainserver.GetMessagesQuery
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named

class MessageMainServerApiImp : MessageMainServerApi {
    private val mApolloClient: ApolloClient
    private val mRetrofit: Retrofit

    @Inject
    constructor(apolloClient: ApolloClient,@Named(DaggerNamed.Retrofit.MainServerRetrofit) retrofit: Retrofit) {
        mApolloClient = apolloClient;
        mRetrofit = retrofit
    }

    override suspend fun sendTextMessage(messageBody: String, channelId: String) {
        try {
            val reqBody = SendTextMessageReqBody(messageBody, channelId)
            val client = mRetrofit.create(MessageMainServerRestClientConfig::class.java)
            val response = client.sendTextMessage(reqBody)

            if (!response.isSuccessful) {
                throw GeneralErrorEntity("Internal server error")
            }
        } catch (e: HttpException) {
            throw NetworkErrorEntity(e.message()?:"")
        }
    }

    override suspend fun getChannelMessage(channelId: String, count: Int) : List<MessageMainServerModel> {
        try {
            val response = mApolloClient.query(GetMessagesQuery(count, channelId)).await()

            if (response.hasErrors()) {
                val currentError = response.errors!![0].simple()
                if (currentError.code == 404) {
                    throw NotFoundErrorEntity("Couldn't find your message")
                }
                throw GeneralErrorEntity("Internal server error: " + currentError.message)
            }
            val result : List<GetMessagesQuery.GetMessage?> = response.data?.getMessages?:throw NotFoundErrorEntity("Couldn't find your message")
            return result.map { queryResult ->
                normalLog("MESSAGE: " + queryResult!!.content)
                MessageMainServerModel(queryResult!!.id, queryResult.type, queryResult.content, queryResult.createdDate.toString().toLong(), queryResult.channelId)
            }
        } catch (e: ApolloException) {
            throw NetworkErrorEntity(e.message?:"")
        }
    }
}