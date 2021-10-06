package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.devlogs.chatty.common.application.SharedMemory
import com.devlogs.chatty.common.helper.convertDpToPx
import com.devlogs.chatty.common.helper.normalLog
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.viewholder.TextChatViewHolder
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.common.viewholder.ItemLoadingViewHolder
import java.util.*

private const val TEXT_CHAT = 1
private const val VIDEO_CHAT = 2
private const val URL_CHAT = 3
private const val BALANCE_SIZE = 4
private const val LOAD_MORE_TYPE = 5


class ChatRcvAdapter : Adapter<RecyclerView.ViewHolder> {

    private var source: TreeSet<ChatPresentableModel> = TreeSet()
    private var sharedBox: ChatAdapterSharedBox
    var isLoading: Boolean = false
    var isLoadMoreEnable = true;  set(value) {
        field = value
        notifyItemChanged(0)
    }
    var onLoadMore : (() -> Unit)? = null
    constructor(sharedBox: ChatAdapterSharedBox) {
        this.sharedBox = sharedBox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TEXT_CHAT) {
            return TextChatViewHolder(inflater, parent, source, sharedBox)
        }
        if (viewType == LOAD_MORE_TYPE) {
            return ItemLoadingViewHolder(inflater, parent)
        }
        val balanceItemView = LinearLayout(parent.context)
        balanceItemView.layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                convertDpToPx(parent.context, 120f).toInt()
            )
        return object : RecyclerView.ViewHolder(balanceItemView) {}
    }

    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var visibleThreadHold = 2
    fun setRecyclerView (rcv: RecyclerView) {
        val layoutManager = rcv.layoutManager as LinearLayoutManager
        rcv.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = layoutManager.itemCount
                lastVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (isLoadMoreEnable && !isLoading && 0 >= (lastVisibleItem-visibleThreadHold)) {
                    onLoadMore?.invoke()
                    isLoading = true
                }
            }
        })
    }

    private fun getDataPosition (currentItemPosition: Int) : Int{
        return currentItemPosition  - 1;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        normalLog("Sender: ${SharedMemory.email}")
        if (holder is TextChatViewHolder) {
            holder.bind(
                source.elementAt(getDataPosition(position)),
                getDataPosition(position),
                if (source.elementAt(getDataPosition(position)).senderEmail.equals("mingting15@mintin.com")) TextChatViewHolder.Type.ORIGIN else TextChatViewHolder.Type.OPPOSITE
            )
        }
        if (holder is ItemLoadingViewHolder) {
            holder.bind(isLoadMoreEnable)
        }
    }

    override fun getItemCount(): Int = source.size + 1+ 1 // plus the balance_size item plus loadMore item

    fun clear () {
        source.clear()
    }

    fun addOldMessage (data: TreeSet<ChatPresentableModel>) {
        if (data.isEmpty()) return
        val previousSize = source.size
        source.addAll(data)
        if (source.size == previousSize) return
        notifyItemRangeInserted(1, data.size-1)
        notifyItemRangeChanged(data.size-1, data.size + 1)
    }

    fun getItemPositionBaseDataPosition ( dataPosition: Int) : Int {
        return dataPosition + 1;
    }

    fun addNewMessages (data: TreeSet<ChatPresentableModel>) {
        if (data.isEmpty()) return
        val indexBeforeChanged = getItemPositionBaseDataPosition(source.size-1)
        val previousSize = source.size
        if (data.size == 1) {
            source.add(data.elementAt(0))
            // if after add the size didn't change => we already add
            if (source.size == previousSize) return
            notifyItemInserted(indexBeforeChanged + 1)
            if (source.size > 1) {
                notifyItemChanged(indexBeforeChanged)
            }
        } else {
            source.addAll(data)
            if (source.size == previousSize) return
            if (source.size > data.size) {
                notifyItemRangeInserted(indexBeforeChanged+1, data.size)
                notifyItemChanged(indexBeforeChanged)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return BALANCE_SIZE
        }
        if (position == 0) {
            return LOAD_MORE_TYPE
        }
        return TEXT_CHAT
    }

    fun updateMessageState(message: ChatPresentableModel, identify: String?) {
        var id = identify;
        if (id == null) {
            id = message.id;
        }

        val index = source.indexOfFirst { it.id.equals(id) }

        val target = source.elementAt(index)

        target.content = message.content
        target.id = message.id
        target.createdDate = message.createdDate
        target.senderEmail = message.senderEmail
        target.type = message.type
        target.state = message.state

        notifyItemChanged((index) + 1)

    }

}