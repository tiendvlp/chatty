package com.devlogs.chatty.screen.mainscreen.account_screen.mvc_view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.devlogs.chatty.R
import com.devlogs.chatty.screen.common.mvcview.BaseMvcView
import com.devlogs.chatty.screen.common.mvcview.UIToolkit
import de.hdodenhof.circleimageview.CircleImageView

class AccountMvcViewImp : BaseMvcView<AccountMvcView.Listener>, AccountMvcView {

    private lateinit var imgAvatar: CircleImageView
    private lateinit var btnSignOut: Button
    private lateinit var btnInviteChannel: ImageButton

    constructor(toolKit: UIToolkit, container: ViewGroup?) {
        setRootView(toolKit.layoutInflater.inflate(R.layout.layout_account, container, false))
        addControls ()
        addEvents ()
    }

    private fun addControls() {
        imgAvatar = findViewById(R.id.imgAvatar)
        btnSignOut = findViewById(R.id.btnSignOut)
        btnInviteChannel = findViewById(R.id.btnInviteChannel)
    }

    private fun addEvents() {
        btnSignOut.setOnClickListener {
            getListener().forEach {
                it.onBtnSignOutClicked()
            }
        }

        btnInviteChannel.setOnClickListener {
            getListener().forEach {
                it.onBtnInviteChannelClicked()
            }
        }
    }


}