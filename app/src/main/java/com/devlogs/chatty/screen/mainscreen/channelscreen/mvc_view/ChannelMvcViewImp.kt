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
import com.devlogs.chatty.common.helper.warningLog
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
    private lateinit var itemAnimator: RecyclerView.ItemAnimator
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
        mLvChannel.adapter = mChannelAdapter
        mChannelAdapter.setSource(loadedChannels)
        itemAnimator = mLvChannel.itemAnimator!!
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

    override fun showReloadedChannel(channels: TreeSet<ChannelPresentationModel>) {
        coroutineScope.launch (BackgroundDispatcher) {
            if (channels.isEmpty()) {
                hideRefresh()
                return@launch
            }

            val useAnim = channels.size <= 8

            val delayTime : Long
            if (useAnim) {
                delayTime = 250
                mLvChannel.itemAnimator = itemAnimator
            } else {
                delayTime = 100
                mLvChannel.itemAnimator = null
            }
            val insertedChannel = TreeSet<ChannelPresentationModel> ()
            var isUpdateProcess: Boolean
            channels.forEach { newChannel ->
                isUpdateProcess = false
                for (i in 0 until  loadedChannels.size) {
                    if (newChannel.compareTo(loadedChannels.elementAt(i)) == 0) {
                        isUpdateProcess = true
                        loadedChannels.remove(newChannel)
                        insertedChannel.add(newChannel)
                        withContext(Dispatchers.Main.immediate) {
                            if (i != 0) {
                                mChannelAdapter.notifyItemMoved(i + 1, 1)
                                delay(delayTime)
                            }
                            mChannelAdapter.notifyItemChanged(1)
                        }
                        break
                    }
                }

                if (!isUpdateProcess) {
                    // insert process
                    insertedChannel.add(newChannel)
                }
            }
            if (insertedChannel.isNotEmpty()) {
                loadedChannels.addAll(insertedChannel)
                withContext(Dispatchers.Main.immediate) {
                    mChannelAdapter.notifyItemRangeInserted(1, insertedChannel.size-1)
                }
            }
                hideRefresh()
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
        loadedChannels.clear()
        loadedChannels.addAll(channels)
        mChannelAdapter.notifyDataSetChanged()
        loadingMvcView.success()
    }

    override fun showMoreChannel(channels: TreeSet<ChannelPresentationModel>) {
        loadedChannels.addAll(channels)
        mChannelAdapter.notifyDataSetChanged()
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
        warningLog("currentThreadName: " + Thread.currentThread().name)
        txtErrorMessage.text = ""
        txtErrorMessage.visibility = View.GONE
    }

    override fun showRefresh() {
        swipeToRefresh.isRefreshing = true
        hideError()
    }


    override fun showTopError (errorType: ErrorType) {
        hideRefresh()
        warningLog("currentThreadName: " + Thread.currentThread().name)
        txtErrorMessage.visibility = View.VISIBLE
        if (errorType == ErrorType.Network) {
            txtErrorMessage.text = "Connection error"
        }
    }
}
