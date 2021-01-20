package com.devlogs.chatty.screen.common.presentationstate

import java.lang.Exception

class InvalidActionException(stateName: String, actionName: String) : Exception("$stateName state can not consume $actionName action")