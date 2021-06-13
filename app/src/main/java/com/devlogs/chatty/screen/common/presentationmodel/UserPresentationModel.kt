package com.devlogs.chatty.screen.common.presentationmodel

import android.graphics.Bitmap
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.toBitmap
import com.devlogs.chatty.common.mapper.Mapper
import com.devlogs.chatty.domain.entity.AccountEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

data class UserPresentationModel (val avatar: Bitmap, val name: String, val email: String)


