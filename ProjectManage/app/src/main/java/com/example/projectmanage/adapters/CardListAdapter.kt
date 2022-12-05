package com.example.projectmanage.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.activities.TaskListActivity
import com.example.projectmanage.databinding.ItemCardBinding
import com.example.projectmanage.models.Card
import com.example.projectmanage.models.SelectedMembers

class CardListAdapter(val context: Context,var datalist:ArrayList<Card>):
    RecyclerView.Adapter<CardListAdapter.ViewHolder>() {

    private var onClickListener:OnClickListener? = null

    class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int){
        val model = datalist[position]
        holder.binding.tvCardName.setText(model.title)

        if(!model.color.isNullOrEmpty()){
            holder.binding.viewLabelColor.visibility = View.VISIBLE
            holder.binding.viewLabelColor.setBackgroundColor(Color.parseColor(model.color))
        }
        else{
            holder.binding.viewLabelColor.visibility = View.GONE
        }
        holder.binding.root.setOnClickListener {
            onClickListener?.onClick(position)
        }



        if((context as TaskListActivity).mBoardMemberDetails.size>0){
            val selectedMembersList=ArrayList<SelectedMembers>()

            for(user in context.mBoardMemberDetails){
               for(i in model.assignedTo){
                   if(i == user.id){
                       selectedMembersList.add(SelectedMembers(user.id,user.image))
                       continue
                   }
               }
            }

            if(selectedMembersList.size>0){
                holder.binding.rvCardSelectedMembersList.visibility = View.VISIBLE
                val adapter = SelectedCardListItemsAdapter(context,selectedMembersList,false)
                holder.binding.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context,4)
                holder.binding.rvCardSelectedMembersList.adapter  = adapter
                adapter.clickListener = object : SelectedCardListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        if(onClickListener!=null){
                            onClickListener!!.onClick(position)
                        }
                    }
                }
            }
        }
        else{
            holder.binding.rvCardSelectedMembersList.visibility = View.GONE
        }
    }

    interface OnClickListener{
        fun onClick(cardPosition: Int)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

}