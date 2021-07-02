package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.resource.GetAvatarUseCaseSync
import com.devlogs.chatty.resource.GetUserAvatarUrlUseCaseSync
import com.devlogs.chatty.resource.UrlType.LOCAL
import com.devlogs.chatty.screen.common.viewholder.ItemLoadingViewHolder
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

class ChannelRcvAdapter : RecyclerView.Adapter<ViewHolder> {

    class ChannelViewHolder : ViewHolder {
        private val imgAvatar: CircleImageView
        private val txtTitle: TextView
        private val txtSender: TextView
        private val txtMessage: TextView
        private val getAvatarUrl: GetUserAvatarUrlUseCaseSync
        private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
        constructor(
            view: View,
            getAvatarUrl: GetUserAvatarUrlUseCaseSync
        ) : super(view) {
            imgAvatar = view.findViewById(R.id.imgAvatar)
            txtMessage = view.findViewById(R.id.txtMessageBody)
            txtTitle = view.findViewById(R.id.txtChannelTitle)
            txtSender = view.findViewById(R.id.txtSenderName)
            this.getAvatarUrl = getAvatarUrl
        }

        fun bind (channel: ChannelPresentationModel) {
            if (layoutPosition == 0) {
                itemView.setPadding(10, 100, 10, 10)
            }

            coroutineScope.launch {
                val result = getAvatarUrl.execute(channel.displayedElement[0]);
                if (result is GetUserAvatarUrlUseCaseSync.Result.Success) {
                    if (result.type == LOCAL) {
                        Glide
                            .with(itemView.context)
                            .load(File(result.url))
                            .centerCrop()
                            .into(imgAvatar);
                    } else {
                        Glide
                            .with(itemView.context)
                            .load(result.url)
                            .centerCrop()
                            .into(imgAvatar);
                    }

                }
            }

            txtMessage.text = channel.message
            txtSender.text = channel.sender
            txtTitle.text = channel.title
        }
    }

    class SearchViewHolder : ViewHolder {
        constructor(view: View) : super(view)
    }

    private val SEARCH_TYPE = 0
    private val CHANNEL_TYPE = 2
    private val LOAD_MORE_TYPE = 3

    private  var channelSources: TreeSet<ChannelPresentationModel> = TreeSet()
    var isLoading: Boolean = false
    var isLoadMoreEnable = true;  set(value) {
       field = value
       notifyItemChanged(channelSources.size + 1)
    }
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var visibleThreadHold = 3
    private val getAvatarUrl: GetUserAvatarUrlUseCaseSync
    var onLoadMore : (() -> Unit)? = null
    @Inject
    constructor(
        getAvatarUrl: GetUserAvatarUrlUseCaseSync
    ) {
        this.getAvatarUrl = getAvatarUrl
    }

    fun setSource (channelSources: TreeSet<ChannelPresentationModel>) {
        this.channelSources = channelSources
        notifyDataSetChanged()
    }

    fun setRecyclerView (rcv: RecyclerView) {
        val layoutManager = rcv.layoutManager as LinearLayoutManager
        rcv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = layoutManager.itemCount
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (isLoadMoreEnable && !isLoading && totalItemCount <= (lastVisibleItem+visibleThreadHold)) {
                    onLoadMore?.invoke()
                    isLoading = true
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        if (viewType == SEARCH_TYPE) {
            return SearchViewHolder (inflater.inflate(R.layout.item_search_channel, parent, false))
        }
        if (viewType == CHANNEL_TYPE) {
            return ChannelViewHolder(
                inflater.inflate(R.layout.item_channel_main, parent, false),
                getAvatarUrl
            )
        }
        return ItemLoadingViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        normalLog("ChannelSize: ${channelSources.size}")
        if (holder is ChannelViewHolder) {
            val channel = channelSources.elementAt(position - 1)
            holder.bind(channel)
            return
        }

        if (holder is ItemLoadingViewHolder) {
            holder.bind(isLoadMoreEnable)
        }

    }

    override fun getItemCount(): Int = channelSources.size + 2

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return SEARCH_TYPE
        }
        if (channelSources.elementAtOrNull(position-1) == null) {
            return LOAD_MORE_TYPE
        }
        return CHANNEL_TYPE
    }
}