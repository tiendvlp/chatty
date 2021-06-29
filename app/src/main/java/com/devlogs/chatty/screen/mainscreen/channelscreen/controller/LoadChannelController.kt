package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import com.devlogs.chatty.channel.*
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.common.helper.warningLog
import com.devlogs.chatty.domain.datasource.local.TokenOfflineApi
import com.devlogs.chatty.resource.GetAvatarUseCaseSync
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.to
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationAction.*
import com.devlogs.chatty.user.GetAccountUseCase
import io.socket.client.Socket
import com.devlogs.chatty.common.helper.*
import com.devlogs.chatty.domain.entity.AccountEntity
import com.devlogs.chatty.domain.error.AuthenticationErrorEntity
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class LoadChannelController {
    private val mPresentationStateManager: PresentationStateManager
    private val mGetChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private val loadMoreChannel: LoadMoreUseCaseSync
    private val reloadChannelUseCaseSync : ReloadChannelUseCaseSync
    private val getLoadedChannel: GetLoadedChannelUseCaseSync
    private val getAccountUseCase: GetAccountUseCase
    private val socketIOInstance: Socket
    private val tokenApi: TokenOfflineApi
    private val getUserAvatarUseCase: GetAvatarUseCaseSync
    @Inject
    constructor(
            getLoadedChannelUseCaseSync: GetLoadedChannelUseCaseSync,
            getChannelOverPeriodOfTimeUseCaseSync: GetUserChannelsOverPeriodOfTimeUseCaseSync,
            presentationStateManager: PresentationStateManager,
            loadMoreChannel: LoadMoreUseCaseSync,
            reloadChannelUseCaseSync: ReloadChannelUseCaseSync,
            getAccountUseCase: GetAccountUseCase,
            socketIOInstance: Socket,
            tokenApi: TokenOfflineApi,
            getUserAvatarUseCase: GetAvatarUseCaseSync
    ) {
        this.getLoadedChannel = getLoadedChannelUseCaseSync
        this.reloadChannelUseCaseSync = reloadChannelUseCaseSync
        mGetChannelOverPeriodOfTimeUseCaseSync = getChannelOverPeriodOfTimeUseCaseSync
        mPresentationStateManager = presentationStateManager
        this.loadMoreChannel = loadMoreChannel
        this.getAccountUseCase = getAccountUseCase
        this.socketIOInstance = socketIOInstance
        this.tokenApi = tokenApi
        this.getUserAvatarUseCase = getUserAvatarUseCase
         try {
            socketIOInstance.emit("joinRoom", tokenApi.getAccessToken())
        } catch (e: AuthenticationErrorEntity.InvalidAccessTokenErrorEntity) {
            errorLog("Access token expired")
        }
    }

    fun reloadChannels () {
        scope.launch (Dispatchers.Main.immediate) {
                val result = reloadChannelUseCaseSync.execute()
            normalLog("ReloadChannelResult: ${result}")
            when (result) {
                is ReloadChannelUseCaseSync.Result.NetworkError -> {
                    normalLog("ReLoad channel failed due to network error")
                    mPresentationStateManager.consumeAction(
                        ReloadChannelFailedAction
                    )
                    return@launch
                }
                is ReloadChannelUseCaseSync.Result.GeneralError -> {
                    normalLog("ReLoad channel failed due to general error")
                    mPresentationStateManager.consumeAction(
                        ReloadChannelFailedAction
                    )
                }
                is ReloadChannelUseCaseSync.Result.UnAuthorized -> {
                    normalLog("Reload channel failed invalid refreshtoken")
                    mPresentationStateManager.consumeAction(
                        ReloadChannelFailedAction
                    )
                }
                is ReloadChannelUseCaseSync.Result.Success -> {
                    if (result.channels.size > 0) {
                        normalLog("Reload channel success: " + result.channels[0].status.content)
                    } else {
                        normalLog("No channels update")
                    }
                    val channelPMS = TreeSet<ChannelPresentationModel>()
                    channelPMS.addAll(result.channels.to())
                    mPresentationStateManager.consumeAction(ReLoadChannelSuccessAction(channelPMS))
                }
            }
        }
    }

    fun loadMoreChannels() {
        scope.launch(Dispatchers.Main.immediate) {
            warningLog("LoadMoreChannels execute")
            val result = loadMoreChannel.execute();
            if (result is LoadMoreUseCaseSync.Result.Success) {
                val channelPMS = TreeSet<ChannelPresentationModel>()
                    channelPMS.addAll(result.channels.to())
                warningLog("LoadMoreChannels execute success: ${channelPMS.size}")
                mPresentationStateManager.consumeAction(LoadMoreChannelSuccessAction(channelPMS))
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.Empty) {
                mPresentationStateManager.consumeAction(LoadMoreChannelSuccessAction(TreeSet<ChannelPresentationModel>()))
            }
            if (result is LoadMoreUseCaseSync.Result.NetworkError) {
                normalLog("Load channel failed due to network error")
                mPresentationStateManager.consumeAction(
                    LoadMoreChannelFailedAction(
                        "Network Error"
                    )
                )
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.GeneralError) {
                normalLog("Load channel failed due to general error")
                mPresentationStateManager.consumeAction(
                    LoadMoreChannelFailedAction(
                        "General Error"
                    )
                )
                return@launch
            }
            if (result is LoadMoreUseCaseSync.Result.UnAuthorized) {
                normalLog("Load channel failed invalid refreshtoken")
                mPresentationStateManager.consumeAction(
                    LoadMoreChannelFailedAction(
                        "UnAuthorized"
                    )
                )
                return@launch
            }
        // ddawjng minh tieens ddawjng minh tieens
        }
    }

    fun getLoadedChannel () {
        scope.launch(Dispatchers.Main.immediate) {
            val result = getLoadedChannel.execute()
            val channelPMS = TreeSet<ChannelPresentationModel>()
            channelPMS.addAll(result.to())
            normalLog("Load channel success: " + channelPMS.size)
            mPresentationStateManager.consumeAction(LoadChannelSuccessAction(channelPMS))
        }
    }

    fun getMyUser () {
        scope.launch {
            val result = getAccountUseCase.execute()
            when (result) {
                is GetAccountUseCase.Result.NetworkError -> {
                    mPresentationStateManager.consumeAction(LoadUserFailedAction)
                }
                is GetAccountUseCase.Result.GeneralError -> {
                    mPresentationStateManager.consumeAction(LoadUserFailedAction)
                }
                is GetAccountUseCase.Result.Success -> {
                    SharedMemory.let {
                        it.email = result.accountEntity.email
                        it.name = result.accountEntity.name
                    }
                }
            }
        }
    }

    suspend fun  convertToUserPresentationModel (account: AccountEntity) {
        val getAvatarResult = getUserAvatarUseCase.execute(account.email)

        if (getAvatarResult is GetAvatarUseCaseSync.Result.Success) {
            mPresentationStateManager.consumeAction(LoadUserSuccessAction(
                    UserPresentationModel(getAvatarResult.bytes.toBitmap(), account.name, account.email)
            ))
            return
        }
        if (getAvatarResult is GetAvatarUseCaseSync.Result.GeneralError) {
            mPresentationStateManager.consumeAction(LoadUserFailedAction)
            return
        }
    }

}
