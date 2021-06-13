package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.devlogs.chatty.R
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.common.helper.toBitmap
import com.devlogs.chatty.resource.GetAvatarUseCaseSync
import com.devlogs.chatty.screen.common.viewholder.ItemLoadingViewHolder
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ChannelRcvAdapter : RecyclerView.Adapter<ViewHolder> {

    class ChannelViewHolder : ViewHolder {
        private val imgAvatar: CircleImageView
        private val txtTitle: TextView
        private val txtSender: TextView
        private val txtMessage: TextView
        private val getAvatarUseCaseSync: GetAvatarUseCaseSync

        constructor(view: View, getAvatarUseCaseSync: GetAvatarUseCaseSync) : super(view) {
            imgAvatar = view.findViewById(R.id.imgAvatar)
            txtMessage = view.findViewById(R.id.txtMessageBody)
            txtTitle = view.findViewById(R.id.txtChannelTitle)
            txtSender = view.findViewById(R.id.txtSenderName)
            this.getAvatarUseCaseSync = getAvatarUseCaseSync
        }

        fun bind (channel: ChannelPresentationModel) {
            if (layoutPosition == 0) {
                itemView.setPadding(10, 100, 10, 10)
            }

            CoroutineScope(Dispatchers.Main.immediate).launch {
                val result = getAvatarUseCaseSync.execute(channel.displayedElement[0])
                if (result is GetAvatarUseCaseSync.Result.Success) {
                    imgAvatar.setImageBitmap(result.bytes.toBitmap())
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

    private val getAvatarUseCaseSync: GetAvatarUseCaseSync;
    private  var channelSources: TreeSet<ChannelPresentationModel> = TreeSet()
    private var isLoading: Boolean = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var visibleThreadHold = 3
    var onLoadMore : (() -> Unit)? = null

    @Inject
    constructor(getAvatarUseCaseSync: GetAvatarUseCaseSync) {
        this.getAvatarUseCaseSync = getAvatarUseCaseSync
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
                if (!isLoading && totalItemCount <= (lastVisibleItem+visibleThreadHold)) {
                    onLoadMore?.invoke()
                    isLoading = true
                }
            }
        })    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        if (viewType == SEARCH_TYPE) {
            return SearchViewHolder (inflater.inflate(R.layout.item_search_channel, parent, false))
        }
        if (viewType == CHANNEL_TYPE) {
            return ChannelViewHolder(inflater.inflate(R.layout.item_channel_main, parent, false), getAvatarUseCaseSync)

        }
        return ItemLoadingViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ChannelViewHolder) {
            val channel = channelSources.elementAt(position - 1)
            normalLog("Binding View Holder: ${channel.id}")
            holder.bind(channel)
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