package com.example.projectmanage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ItemBoardBinding
import com.example.projectmanage.models.Board

class BoardItemsAdapter(val context: Context,private var datalist:ArrayList<Board>):RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    var clickListener:OnClickListener? = null

    class ViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick(position: Int,model:Board)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val boardInfo = datalist[position]

        Glide.with(context).load(boardInfo.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.binding.ivBoardImage as ImageView)

        holder.binding.tvCreatedBy.setText(" Created by: ${boardInfo.createdBy}")
        holder.binding.tvName.setText(boardInfo.name)

        holder.binding.root.setOnClickListener{
            clickListener?.onClick(position,boardInfo)
        }
    }

}