package com.example.projectmanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.R
import com.example.projectmanage.adapters.LabelColorListItemsAdapter

abstract class LabelColorListDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val title: String = "",
    private val mSelectedColor: String = ""
) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: Any){
        val tvText = this.findViewById<TextView>(R.id.tvTitle)
        val rvList = this.findViewById<RecyclerView>(R.id.rvList)

        tvText.setText(title)
        rvList.layoutManager = LinearLayoutManager(context)
        val adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        rvList.adapter = adapter

        adapter.onClickListener = object : LabelColorListItemsAdapter.OnClickListener {

            override fun onClick(position: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }
    }

    protected abstract fun onItemSelected(color:String)
}