package com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.devlogs.chatty.R
import com.devlogs.chatty.common.background_dispatcher.BackgroundDispatcher
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.LoadingMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.common.presentationstate.PresentationState
import com.devlogs.chatty.screen.common.presentationstate.PresentationStateManager
import com.devlogs.chatty.screen.mainscreen.channelscreen.controller.ChannelRcvAdapter
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView.*
import com.devlogs.chatty.screen.mainscreen.channelscreen.state.ChannelScreenPresentationState.DisplayState
import kotlinx.coroutines.*
import java.util.*

class ChannelMvcViewImp : BaseMvcView<Listener>, ChannelMvcView,
    ChannelToolbarMvcView.Listener {
    private lateinit var mLvChannel: RecyclerView
    private var mChannelAdapter: ChannelRcvAdapter
    private val loadedChannels: TreeSet<ChannelPresentationModel> = TreeSet()
    private lateinit var mToolbarMvcSubView: ChannelToolbarMvcView
    private lateinit var mToolbarElement: Toolbar
    private lateinit var txtErrorMessage: TextView
    private lateinit var mainLayout: ViewGroup
    private lateinit var loadingLayoutContainer: FrameLayout
    private lateinit var loadingMvcView: LoadingMvcView
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private val coroutineScope = CoroutineScope(BackgroundDispatcher)
    private val toolKit: UIToolkit
    private val screenStateManager: PresentationStateManager
    private lateinit var lvChannelLayoutManager: LinearLayoutManager
    constructor(
        toolKit: UIToolkit,
        container: ViewGroup?,
        channelRcvAdapter: ChannelRcvAdapter,
        screenStateManager: PresentationStateManager
    ) {
        this.screenStateManager = screenStateManager
        this.toolKit = toolKit
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_channel, container, false))
        addControls()
        normalLog("constructor mvcView")
        mChannelAdapter = channelRcvAdapter
        setup(toolKit.layoutInflater)
        addEvents()
        restore(screenStateManager.currentState)
    }

    private fun restore (currentState: PresentationState) {
        if (currentState is DisplayState) {
            display(currentState.channels)
            normalLog("Restore scroll position: ${currentState.scrollPosition}")
            mLvChannel.scrollToPosition(currentState.scrollPosition);
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setup(layoutInflater: LayoutInflater) {
        mToolbarMvcSubView = ChannelToolbarMvcView(toolKit, mToolbarElement)
        mToolbarElement.addView(mToolbarMvcSubView.getRootView())

         lvChannelLayoutManager =
            LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        mLvChannel.layoutManager = lvChannelLayoutManager
        mChannelAdapter.setRecyclerView(mLvChannel)
        mChannelAdapter.setSource(loadedChannels)
        mLvChannel.adapter = mChannelAdapter
        mChannelAdapter.isLoadMoreEnable = false
    }

    private fun addControls() {
        swipeToRefresh = findViewById(R.id.swipeToRefresh)
        loadingLayoutContainer = findViewById(R.id.layoutLoadingContainer)
        mainLayout = findViewById(R.id.mainLayout)
        mToolbarElement = findViewById(R.id.toolbar)
        mLvChannel = findViewById(R.id.lvChannel)
        txtErrorMessage = findViewById(R.id.txtErrorMessage)
        loadingMvcView = LoadingMvcView(LayoutInflater.from(getContext()), loadingLayoutContainer)
        loadingLayoutContainer.addView(loadingMvcView.getRootView())
    }

    private fun addEvents() {
        mChannelAdapter.onChannelClicked = ::onChannelClicked
        mToolbarMvcSubView.register(this)
        swipeToRefresh.setOnRefreshListener {
            getListener().forEach {
                it.onRefreshChannel()
            }
        }
        mChannelAdapter.onLoadMore = {
            getListener().forEach {
                normalLog("LoadMore event")
                it.onLoadMoreChannel()
            }
        }
    }

    override fun hideRefresh() {
        swipeToRefresh.isRefreshing = false
        hideError()
    }

    override fun showUserInfo(user: UserPresentationModel) {
        mToolbarMvcSubView.changeImage(user.avatar)
    }

    override fun showLoadUserError() {
        mToolbarMvcSubView.error()
    }

    override fun showNewChannel(newChannel: ChannelPresentationModel) {
        normalLog("Item inserted")
        insertProcess(newChannel)
    }

    /**
     * Sync database with ui
     * Only run animation when the changed channels less than 8
     * If not we run notifydatasetchanged
     * First we detect which channel need to be: Update/Insert/Skip (Skip because
     * in some cases channel was inserted/updated before sync, so that's mean
     * it's already synced, we don't need to sync it again
     * We call delay after adapter.notify... to let the adapter run it's anim])
     * */

    override fun showReloadedChannel(channels: TreeSet<ChannelPresentationModel>) {
        if (channels.isEmpty()) {
            hideRefresh()
            return
        }
        val useAnim = channels.size < 8
        var loadedChannelCache: ChannelPresentationModel?
        var replacedIndex: Int
        var isUpdateProcess: Boolean
        var isInsertedProcess: Boolean

        channels.forEach { newChannel ->
            replacedIndex = -1
            isUpdateProcess = false
            isInsertedProcess = true
            loadedChannels.forEachIndexed loaded@{ index, loadedChannel ->
                loadedChannelCache = loadedChannel
                if (newChannel.id.equals(loadedChannelCache!!.id)) {
                    isInsertedProcess = false
                    if (newChannel.lastUpdate != loadedChannelCache!!.lastUpdate) {
                        replacedIndex = index
                        isUpdateProcess = true
                        return@loaded
                    }
                }
            }
            if (isUpdateProcess) {
                updateProcess(newChannel, replacedIndex, useAnim)
            } else if (isInsertedProcess) {
                insertProcess(newChannel)
            }
        }
        if (!useAnim) {
            mChannelAdapter.notifyDataSetChanged()
        }
        hideRefresh()
    }


    override fun updateChannel(channel: ChannelPresentationModel) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            val replacedIndex = loadedChannels.indexOfFirst { it.compareTo(channel) == 0 }
            if (replacedIndex == -1) return@launch
            updateProcess(channel, replacedIndex)
        }
    }

    private fun insertProcess (channel: ChannelPresentationModel, useAnim: Boolean = true) {
        loadedChannels.add(channel)
        if (useAnim) {
            mChannelAdapter.notifyItemInserted(1)
        }
    }

    private fun updateProcess (channel: ChannelPresentationModel, replacedIndex: Int, useAnim: Boolean = true) {
        loadedChannels.removeIf { it.id.equals(channel.id)}
        loadedChannels.add(channel)
        if (useAnim) {
            if (replacedIndex == 0) {
                mChannelAdapter.notifyItemChanged(1)
            } else {
                mChannelAdapter.notifyItemMoved(replacedIndex + 1, 1)
                mChannelAdapter.notifyItemChanged(1)

            }
        }
    }

    private fun onChannelClicked (channel: ChannelPresentationModel) {
        getListener().forEach { listener ->
            listener.onUserSelectedChannel(channel)
        }
    }

    override fun saveState() {
        if (screenStateManager.currentState is DisplayState) {
           (screenStateManager.currentState as DisplayState).scrollPosition = lvChannelLayoutManager.findFirstVisibleItemPosition()
        }
        val v: View = lvChannelLayoutManager.getChildAt(0)!!
        val top = if (v == null) 0 else v.top - mLvChannel.paddingTop

    }

    override fun showLoading() {
        loadingMvcView.showLoading()
    }

    override fun showError(errorType: ErrorType) {
        if (errorType == ErrorType.Network) {
            loadingMvcView.showError("Network error")
        }
    }

    override fun hideError() {
        loadingMvcView.hideError()
    }

    override fun display(channels: TreeSet<ChannelPresentationModel>) {
        hideTopError()
        normalLog("Display channels: ${channels.size}")
        loadedChannels.clear()
        loadedChannels.addAll(channels)
        mChannelAdapter.notifyDataSetChanged()
        mChannelAdapter.isLoadMoreEnable= true
        loadingMvcView.success()
    }

    override fun showMoreChannel(channels: TreeSet<ChannelPresentationModel>) {
        if (channels.isEmpty()) {
            mChannelAdapter.isLoadMoreEnable = false
            return
        } else {
            mChannelAdapter.isLoadMoreEnable = true
            loadedChannels.addAll(channels)
            mChannelAdapter.notifyDataSetChanged()
            mChannelAdapter.isLoading = false
        }
    }

    override fun showLoadMoreError(errorType: ErrorType) {
        showTopError(errorType)
    }

    override fun onBtnAccountClicked() {

    }

    override fun onBtnNewChatClicked() {

    }

    override fun hideTopError() {
        hideRefresh()
        txtErrorMessage.text = ""
        txtErrorMessage.visibility = View.GONE
    }

    override fun showRefresh() {
        swipeToRefresh.isRefreshing = true
        hideError()
    }


    override fun showTopError(errorType: ErrorType) {
        hideRefresh()
        txtErrorMessage.visibility = View.VISIBLE
        if (errorType == ErrorType.Network) {
            "Connection error".also { txtErrorMessage.text = it }
        }
    }
}
