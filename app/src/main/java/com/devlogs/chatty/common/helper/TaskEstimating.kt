package com.devlogs.chatty.common.helper

import java.util.*

object TaskEstimating {

    private val date: Date = Date();
    suspend fun <T> estimate(
        task: suspend () -> T,
        callback: (result: TaskEstimatingResult) -> Unit
    ): T {
        val start = Date().time
        val result: T = task()
        val end = Date().time
        val executeTime = end - start;
        callback(TaskEstimatingResult(start, end, executeTime));
        return result
    }

}