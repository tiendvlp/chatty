package com.devlogs.chatty.screen.search_screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.devlogs.chatty.R

class SearchActivity : AppCompatActivity() {
    companion object {
        fun start (context: Context, option: ActivityOptionsCompat) {
            context.startActivity(Intent(context, SearchActivity::class.java), option.toBundle())
        }
    }

    private lateinit var edtSearch : EditText
    private lateinit var btnCancel : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_search)
        addControls()
        addEvents()
    }

    private fun addControls() {
        btnCancel = findViewById(R.id.btnCancel)
        edtSearch = findViewById(R.id.edtSearch)
        edtSearch.requestFocus()
        edtSearch.maxLines = 1
    }

    private fun addEvents() {
        btnCancel.setOnClickListener {
            onBackPressed()
        }

        edtSearch.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
            }
            false
        }
    }
}