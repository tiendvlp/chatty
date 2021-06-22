package com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatAdapterSharedBox
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import de.hdodenhof.circleimageview.CircleImageView


class TextChatViewHolder : RecyclerView.ViewHolder {

    enum class Type {
        OPPOSITE, ORIGIN
    }

    private val txtMessage: TextView
    private val imgAvatar: CircleImageView
    private val dynamicBackground: LinearLayout
    private val resource: List<ChatPresentableModel>
    private val txtSender: TextView
    private val wrapperLayout: LinearLayout
    private val sharedBox: ChatAdapterSharedBox
    val lp = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    constructor(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        resource: List<ChatPresentableModel>,
        sharedBox: ChatAdapterSharedBox
    ) : super(layoutInflater.inflate(R.layout.item_textchat,container, false)) {
        this.resource = resource
        this.sharedBox = sharedBox
        txtMessage = itemView.findViewById(R.id.txtChatMessage)
        wrapperLayout = itemView.findViewById(R.id.wrapperLayout)
        imgAvatar = itemView.findViewById(R.id.imgAvatar)
        txtSender = itemView.findViewById(R.id.txtSender)
        dynamicBackground = itemView.findViewById(R.id.dynamicMessageBackground)
    }

    fun bind (data: ChatPresentableModel, pos: Int, type: Type) {
        val previousItem = resource.getOrNull(pos -1)
        val nextItem = resource.getOrNull(pos + 1)

        var isSameGroupWithPrevious = false
        var isSameGroupWithNext = false

        if (previousItem != null) {
            isSameGroupWithPrevious = previousItem.senderEmail.equals(data.senderEmail)
        }
        if (nextItem != null) {
            isSameGroupWithNext = nextItem.senderEmail.equals(data.senderEmail)
        }


        if (isSameGroupWithNext && isSameGroupWithPrevious){
              dynamicBackground.background = ContextCompat.getDrawable(itemView.context,R.drawable.circularview_rightcornersbg)
        }

        else if (isSameGroupWithPrevious) {
            dynamicBackground.background =
                ContextCompat.getDrawable(itemView.context,R.drawable.circularview_toprightcornerbg)
        }

        else if (isSameGroupWithNext) {
             dynamicBackground.background = ContextCompat.getDrawable(itemView.context,R.drawable.circularview_bottomrightcornerbg)
        } else {
            dynamicBackground.background = ContextCompat.getDrawable(itemView.context,R.drawable.circularviewbg_a)
        }

        var marginTop = 10
        if (isSameGroupWithPrevious) {
            marginTop = 5
        }

        lp.setMargins(itemView.marginLeft, marginTop, itemView.marginRight, itemView.marginBottom)
        wrapperLayout.layoutParams = lp

        if (type == Type.OPPOSITE) {
            if (isSameGroupWithPrevious) {
                txtSender.visibility = View.GONE
            } else {
                txtSender.visibility = View.VISIBLE
            }
            dynamicBackground.backgroundTintList = itemView.context.getColorStateList(R.color.grey_200)
            imgAvatar.visibility = View.VISIBLE
            itemView.scaleX = 1f
            txtMessage.setTextColor(itemView.context.getColor(R.color.black))
            dynamicBackground.scaleX = -1f
            txtMessage.scaleX = -1f
        } else {
            txtSender.visibility = View.GONE
            dynamicBackground.backgroundTintList = itemView.context.getColorStateList(R.color.main_color)
            imgAvatar.visibility = View.GONE
            itemView.scaleX = -1f
            dynamicBackground.scaleX = -1f
            txtMessage.setTextColor(itemView.context.getColor(R.color.white))
            txtMessage.scaleX = 1f
        }
        txtSender.text = data.senderEmail.split("@")[0]
        txtMessage.text = data.content
    }
}