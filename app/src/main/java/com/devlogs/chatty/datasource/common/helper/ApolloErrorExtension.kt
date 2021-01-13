package com.devlogs.chatty.datasource.common.helper

import com.apollographql.apollo.api.Error

data class GraphqlSimpleError(val message: String, var code: Int?)

fun Error.simple () : GraphqlSimpleError {
    return GraphqlSimpleError(message, customAttributes.get("code").toString().toIntOrNull())
}

