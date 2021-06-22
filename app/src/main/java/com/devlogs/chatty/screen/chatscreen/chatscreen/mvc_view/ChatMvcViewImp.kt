package com.devlogs.chatty.screen.chatscreen.chatscreen.mvc_view

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatAdapterSharedBox
import com.devlogs.chatty.screen.chatscreen.chatscreen.controller.chat_adapter.ChatRcvAdapter
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatType
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit

 val spawnMessage : ArrayList<ChatPresentableModel> = arrayListOf(
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Hello, mấy nay khỏe không", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "lâu quá không gặp", 123284238423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Mốt lên đi ún bia", 12329428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Ok t cũng nhớ m ghê", 12322428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Đi chỗ nào", 12332328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Rogue đuy :)) mê chổ đó lắm", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Okay", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Chỗ đó cũng được", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Có mấy loại t cũng chưa thử bao giờ", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Uống xong đi vài vòng Sài gòn chơi, chứ mấy nay nhớ Sài Gòn vl", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Ok m", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Tới với m luôn", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Mà nay m khỏi bao, để t bao cho", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Từ chối quýnh m lìn", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Đm, đỉnh v, ok lun", 12328428423),
     ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Okay <3", 12328428423),
 )

class ChatMvcViewImp : ChatMvcView, BaseMvcView<ChatMvcView.Listener> {

    private val toolbarMvcView: ChatToolbarMvcView
    private val chatBoxMvcView: ChatBoxMvcView
    private val toolbar: Toolbar
    private val chatBoxContainer: FrameLayout
    private val lvChat: RecyclerView
    private val chatRcvAdapter: ChatRcvAdapter

    constructor(toolKit: UIToolkit, container: ViewGroup?) {
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_chat, container, false))
        toolbar = findViewById(R.id.toolbar)
        toolbarMvcView = ChatToolbarMvcView(toolKit, toolbar);
        chatBoxContainer = findViewById(R.id.chatBoxContainer)
        chatRcvAdapter = ChatRcvAdapter(ChatAdapterSharedBox(getContext()))
        toolbar.addView(toolbarMvcView.getRootView())
        chatBoxMvcView = ChatBoxMvcView(toolKit, chatBoxContainer)
        chatBoxContainer.addView(chatBoxMvcView.getRootView())
        lvChat = findViewById(R.id.lvChat)
        lvChat.layoutManager = LinearLayoutManager(getContext())
        chatRcvAdapter.setSource(spawnMessage)
        lvChat.adapter = chatRcvAdapter
    }


}