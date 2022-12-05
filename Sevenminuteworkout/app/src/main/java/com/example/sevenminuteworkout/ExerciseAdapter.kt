package com.example.sevenminuteworkout

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sevenminuteworkout.databinding.ExerciseItemBinding


class ExerciseAdapter(var datalist: ArrayList<ExerciseModel>) :
    RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {


    class ViewHolder(private val binding: ExerciseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var tvItem = binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExerciseItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datalist[position]
        holder.tvItem.text = item.getId().toString()

        if (item.getIsSelected()) {
            holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_selected_bg)
            holder.tvItem.setTextColor(Color.parseColor("#212121"))
        } else if (item.getIsCompleted()) {
            holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_completed_bg)
            holder.tvItem.setTextColor(Color.parseColor("#ffffff"))
        } else {
            holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_grey_bg)
            holder.tvItem.setTextColor(Color.parseColor("#000000"))
        }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

}