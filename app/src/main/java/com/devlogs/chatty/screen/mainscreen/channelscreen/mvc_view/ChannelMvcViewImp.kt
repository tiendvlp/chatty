package com.devlogs.chatty.screen.mainscreen.channelscreen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.mainscreen.channelscreen.controller.ChannelRcvAdapter
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel

class ChannelMvcViewImp : BaseMvcView<ChannelMvcView.Listener>, ChannelMvcView,
    ChannelToolbarMvcView.Listener {

    private lateinit var mLvChannel : RecyclerView
    private val mChannelAdapter: ChannelRcvAdapter
    private val loadedChannels: ArrayList<ChannelPresentationModel> = ArrayList()
    private lateinit var mToolbarMvcSubView: ChannelToolbarMvcView
    private lateinit var mToolbarElement: Toolbar

    constructor(layoutInflater: LayoutInflater, container: ViewGroup?) {
        setRootView(layoutInflater.inflate(R.layout.layout_channel, container, false))
        mChannelAdapter = ChannelRcvAdapter(loadedChannels)
        addControls()
        setup(layoutInflater)
        addEvents()

    }

    private fun setup(layoutInflater: LayoutInflater) {
        mToolbarMvcSubView = ChannelToolbarMvcView(layoutInflater, mToolbarElement)
        mToolbarElement.addView(mToolbarMvcSubView.getRootView())

        val lvChannelLayoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false)
        mLvChannel.layoutManager = lvChannelLayoutManager
        mLvChannel.adapter = mChannelAdapter
    }

    private fun addControls() {
        mToolbarElement = findViewById(R.id.toolbar)
        mLvChannel = findViewById(R.id.lvChannel)
    }

    private fun addEvents() {
        mToolbarMvcSubView.register(this)
    }

    override fun loading() {
        Toast.makeText(getContext(), "Loading", Toast.LENGTH_LONG).show()
    }

    override fun loadingFailed() {
        Toast.makeText(getContext(), "Load Failed", Toast.LENGTH_LONG).show()
    }

    override fun channelLoaded(channels: List<ChannelPresentationModel>) {
        val startedChangedPos = loadedChannels.size
        loadedChannels.addAll(channels)
        mChannelAdapter.notifyItemRangeInserted(startedChangedPos, loadedChannels.size)
    }

    override fun onBtnAccountClicked() {

    }

    override fun onBtnNewChatClicked() {

    }
}
