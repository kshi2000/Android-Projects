package com.example.projectmanage.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.databinding.ItemLabelColorBinding

class LabelColorListItemsAdapter(context: Context,private var datalist:ArrayList<String>,private var mSelectedColor:String=""):
    RecyclerView.Adapter<LabelColorListItemsAdapter.ViewHolder>() {

    var onClickListener:OnClickListener?=null

    class ViewHolder(val binding: ItemLabelColorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelColorListItemsAdapter.ViewHolder {
        val binding = ItemLabelColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LabelColorListItemsAdapter.ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: LabelColorListItemsAdapter.ViewHolder, position: Int){
        val color = datalist[position]
        holder.binding.viewMain.setBackgroundColor(Color.parseColor(color))

        if(color == mSelectedColor){
            holder.binding.ivSelectedColor.visibility = View.VISIBLE
        }

        holder.binding.root.setOnClickListener {
            onClickListener?.onClick(position,color)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,color:String)
    }

    @JvmName("setOnClickListener1")
    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

}