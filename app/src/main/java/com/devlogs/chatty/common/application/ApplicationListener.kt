package com.devlogs.chatty.common.application

interface ApplicationListener {

}

interface ServerConnectionEvent : ApplicationListener {
    fun onServerDisconnected ()
    fun onServerConnected ()
}