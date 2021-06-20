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
import com.devlogs.chatty.screen.common.presentationmodel.UserPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.controller.ChannelRcvAdapter
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view.ChannelMvcView.*
import kotlinx.coroutines.*
import java.util.*

class ChannelMvcViewImp : BaseMvcView<Listener>, ChannelMvcView,
    ChannelToolbarMvcView.Listener {
    private lateinit var mLvChannel : RecyclerView
    private  var mChannelAdapter: ChannelRcvAdapter
    private val loadedChannels: TreeSet<ChannelPresentationModel> = TreeSet()
    private lateinit var mToolbarMvcSubView: ChannelToolbarMvcView
    private lateinit var mToolbarElement: Toolbar
    private lateinit var txtErrorMessage: TextView
    private lateinit var mainLayout: ViewGroup
    private lateinit var loadingLayoutContainer : FrameLayout
    private lateinit var loadingMvcView: LoadingMvcView
    private lateinit var swipeToRefresh: SwipeRefreshLayout
    private val coroutineScope = CoroutineScope(BackgroundDispatcher)

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?, channelRcvAdapter: ChannelRcvAdapter) {
        setRootView(layoutInflater.inflate(R.layout.layout_channel, container, false))
        addControls()
        normalLog("constructor mvcView")
        mChannelAdapter = channelRcvAdapter
        setup(layoutInflater)
        addEvents()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setup(layoutInflater: LayoutInflater) {
        mToolbarMvcSubView = ChannelToolbarMvcView(layoutInflater, mToolbarElement)
        mToolbarElement.addView(mToolbarMvcSubView.getRootView())

        val lvChannelLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        mLvChannel.layoutManager = lvChannelLayoutManager
        mChannelAdapter.setRecyclerView(mLvChannel)
        mChannelAdapter.setSource(loadedChannels)
        mLvChannel.adapter = mChannelAdapter
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

    override fun hideRefresh () {
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
        loadedChannels.add(newChannel)
        mChannelAdapter.notifyItemInserted(0)
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
        coroutineScope.launch (BackgroundDispatcher) {
            normalLog("Length new channel comming: ${channels.size}")
            if (channels.isEmpty()) {
                hideRefresh()
                return@launch
            }
            // TODO: Fix reload channel, it's only works with notifyDataSetchanged, the problems is Adapter notify is not running correctly
            val useAnim = false
            val delayTime : Long = 250

            val insertedChannel = TreeSet<ChannelPresentationModel> ()
            var isUpdateProcess: Boolean
            var loadedChannelCache : ChannelPresentationModel? = null
            var replacedIndex = -1
            var isInsertedProcess = false

            channels.forEach { newChannel ->
                replacedIndex = -1
                isUpdateProcess = false
                isInsertedProcess = false
                this@ChannelMvcViewImp.normalLog("======Start analyzing======")
                for (i in 0 until  loadedChannels.size) {
                    loadedChannelCache = loadedChannels.elementAt(i)
                    this@ChannelMvcViewImp.normalLog("Check: ${loadedChannelCache!!.message}")
                    if (newChannel.id.equals(loadedChannelCache!!.id)
                        && newChannel.lastUpdate != loadedChannelCache!!.lastUpdate) {
                        this@ChannelMvcViewImp.normalLog("Check: UPDATE")
                        replacedIndex = i
                        isUpdateProcess = true
                        isInsertedProcess = false
                        break
                    }
                }
                if (loadedChannelCache != null && !newChannel.id.equals((loadedChannelCache!!.id))) {
                    this@ChannelMvcViewImp.normalLog("Check: INSERT")
                    isInsertedProcess = true
                    isUpdateProcess = false
                }
                if (isUpdateProcess) {
                        loadedChannels.remove(loadedChannelCache)
                        loadedChannels.add(newChannel)
                        if (useAnim) {
                            withContext(Dispatchers.Main.immediate) {
                                if (replacedIndex == 0) {
                                    this@ChannelMvcViewImp.normalLog("Check: UPDATE ANIM 1")
                                    mChannelAdapter.notifyItemChanged(1)
                                    delay(delayTime)
                                } else {
                                    this@ChannelMvcViewImp.normalLog("Check: UPDATE ANIM 2")
                                    mChannelAdapter.notifyItemMoved(replacedIndex + 1, 1)
                                    delay(delayTime)
                                    mChannelAdapter.notifyItemChanged(1)
                                    delay(delayTime)
                                }
                            }
                        }

                } else if (isInsertedProcess) {
                    loadedChannels.remove(loadedChannelCache)
                    loadedChannels.add(newChannel)
                    if (useAnim) {
                        withContext(Dispatchers.Main.immediate) {
                            mChannelAdapter.notifyItemRangeInserted(1, insertedChannel.size)
                            this@ChannelMvcViewImp.normalLog("Check: INSERT ANIM ")
                            delay(delayTime)
                        }
                    }
                }
            }
            if (!useAnim) {
                withContext(Dispatchers.Main.immediate) {
                    mChannelAdapter.notifyDataSetChanged()
                }
            }
            hideRefresh()
        }
    }

    override fun updateChannel(channel: ChannelPresentationModel) {
        coroutineScope.launch(Dispatchers.Main.immediate) {
            val replacedIndex = loadedChannels.indexOfFirst { it.compareTo(channel) == 0 }
            if (replacedIndex == -1) return@launch
            loadedChannels.remove(loadedChannels.elementAt(replacedIndex))
            loadedChannels.add(channel)
            if (replacedIndex == 0) {
                mChannelAdapter.notifyItemChanged(1)
                delay(200)
            } else {
                mChannelAdapter.notifyItemMoved(replacedIndex, 1)
                delay(200)
                mChannelAdapter.notifyItemChanged(1)
            }
        }
    }

    override fun showLoading() {
        loadingMvcView.showLoading()
    }

    override fun showError(errorType: ErrorType) {
        if (errorType == ErrorType.Network) {
            loadingMvcView.showError("Network error")
        }
    }

    override fun hideError () {
        loadingMvcView.hideError()
    }

    override fun display(channels: TreeSet<ChannelPresentationModel>) {
        hideTopError()
        normalLog("Display channels: ${channels.size}")
        loadedChannels.clear()
        loadedChannels.addAll(channels)
        mChannelAdapter.notifyDataSetChanged()
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

    override fun hideTopError () {
        hideRefresh()
        txtErrorMessage.text = ""
        txtErrorMessage.visibility = View.GONE
    }

    override fun showRefresh() {
        swipeToRefresh.isRefreshing = true
        hideError()
    }


    override fun showTopError (errorType: ErrorType) {
        hideRefresh()
        txtErrorMessage.visibility = View.VISIBLE
        if (errorType == ErrorType.Network) {
            txtErrorMessage.text = "Connection error"
        }
    }
}
