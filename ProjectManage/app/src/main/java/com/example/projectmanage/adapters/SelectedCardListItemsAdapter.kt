package com.example.projectmanage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ItemCardSelectedMemberBinding
import com.example.projectmanage.models.SelectedMembers

class SelectedCardListItemsAdapter(val context: Context, private var datalist:ArrayList<SelectedMembers>,var shouldIncludeAddIcon:Boolean):
    RecyclerView.Adapter<SelectedCardListItemsAdapter.ViewHolder>() {

    var clickListener:OnClickListener? = null

    class ViewHolder(val binding: ItemCardSelectedMemberBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnClickListener{
        fun onClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardSelectedMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = datalist[position]

        if(position == datalist.size-1 && shouldIncludeAddIcon){
            holder.binding.ivSelectedMemberImage.visibility = View.GONE
            holder.binding.ivAddMember.visibility = View.VISIBLE
        }
        else{
            Glide.with(context).load(model.image).fitCenter()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivSelectedMemberImage as ImageView)
            holder.binding.ivSelectedMemberImage.visibility = View.VISIBLE
            holder.binding.ivAddMember.visibility = View.GONE
        }

        holder.binding.root.setOnClickListener{
            clickListener?.onClick()
        }
    }

}