package com.devlogs.chatty.common.helper

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun Any.normalLog (message: String) {
    Log.d(this.javaClass.simpleName, message)
}

fun Any.warningLog (message: String) {
    Log.w(this.javaClass.simpleName, message)
}

fun Any.errorLog (message: String) {
    Log.e(this.javaClass.simpleName, message)
}

fun CoroutineScope.printCoroutineScopeInfo() {
    println()
    println("CoroutineScope: $this")
    println("CoroutineContext: ${this.coroutineContext}")
    println("Job: ${this.coroutineContext[Job]}")
    println()
}

fun printJobsHierarchy(job: Job, nestLevel: Int = 0) {
    val indent = "    ".repeat(nestLevel)
    println("$indent- $job")
    for (childJob in job.children) {
        printJobsHierarchy(childJob, nestLevel + 1)
    }
    if (nestLevel == 0) {
        println()
    }
}