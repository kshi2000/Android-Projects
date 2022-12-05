package com.example.projectmanage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ItemMemberBinding
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants

class MemberItemAdapter(val context:Context,var datalist:ArrayList<User>): RecyclerView.Adapter<MemberItemAdapter.ViewHolder>() {

    private var onClickListener:OnClickListener? = null

    class ViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userInfo = datalist[position]

        if(userInfo.selected == 1){
            holder.binding.ivSelectedMember.visibility = View.VISIBLE
        }
        else{
            holder.binding.ivSelectedMember.visibility = View.GONE
        }

        Glide.with(context).load(userInfo.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.binding.ivMemberImage)

        holder.binding.tvMemberEmail.setText(userInfo.email)
        holder.binding.tvMemberName.setText(userInfo.name)

        holder.binding.root.setOnClickListener{
            if(onClickListener!=null){
                if(userInfo.selected != 1){
                    onClickListener!!.onClick(position,userInfo,Constants.SELECT)
                }
                else{
                    onClickListener!!.onClick(position,userInfo,Constants.UNSELECT)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int,user: User,action:String)
    }

}