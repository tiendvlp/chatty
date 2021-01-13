package com.devlogs.chatty.screen.common.customview
import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

@Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
class AutoFitGridLayoutManager(context: Context?, columnWidth: Int) :
    GridLayoutManager(context, 1) {
    private var columnWidth = 0
    private var columnWidthChanged = true

    private fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != columnWidth) {
            columnWidth = newColumnWidth
            columnWidthChanged = true
        }
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        lp!!.width = this.columnWidth
        lp!!.height = this.columnWidth
        return true
    }

    override fun onLayoutChildren(
        recycler: Recycler,
        state: RecyclerView.State
    ) {
        if (columnWidthChanged && columnWidth > 0) {
           val totalSpace = if (orientation == LinearLayoutManager.VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = 1.coerceAtLeast(totalSpace / columnWidth)
            setSpanCount(spanCount)
            columnWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }

    init {
        setColumnWidth(columnWidth)
    }
}