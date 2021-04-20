package com.devlogs.chatty.screen.mainscreen.channelscreen.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.mainscreen.channelscreen.model.ChannelPresentationModel
import de.hdodenhof.circleimageview.CircleImageView

class ChannelRcvAdapter : RecyclerView.Adapter<ViewHolder> {
    class ChannelViewHolder : ViewHolder {
        private val imgAvatar: CircleImageView
        private val txtTitle: TextView
        private val txtSender: TextView
        private val txtMessage: TextView

        constructor(view: View) : super(view) {
            imgAvatar = view.findViewById(R.id.imgAvatar)
            txtMessage = view.findViewById(R.id.txtMessageBody)
            txtTitle = view.findViewById(R.id.txtChannelTitle)
            txtSender = view.findViewById(R.id.txtSenderName)
        }

        fun bind (channelPresentationModel: ChannelPresentationModel) {
            imgAvatar.setImageBitmap(channelPresentationModel.avatar[0])
            txtMessage.text = channelPresentationModel.message
            txtSender.text = channelPresentationModel.sender
            txtTitle.text = channelPresentationModel.title
        }
    }

    class SearchViewHolder : ViewHolder {
        constructor(view: View) : super(view)
    }

    val SEARCH_TYPE = 0
    val CHANNEL_TYPE = 2

    private val channelSources: ArrayList<ChannelPresentationModel>

    constructor(channelSources: ArrayList<ChannelPresentationModel>) {
        this.channelSources = channelSources
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        if (viewType == SEARCH_TYPE) {
            return SearchViewHolder (inflater.inflate(R.layout.item_search_channel, parent, false))
        }
        // CHANEL_TYPE
        return ChannelViewHolder(inflater.inflate(R.layout.item_channel_main, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ChannelViewHolder) {
            holder.bind(channelSources[position-1])
        }
    }

    override fun getItemCount(): Int = channelSources.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return SEARCH_TYPE
        }
        return CHANNEL_TYPE
    }
}