package com.example.sevenminuteworkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sevenminuteworkout.databinding.ItemHistoryRowBinding

class HistoryAdapter(var datalist:ArrayList<String>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


    class ViewHolder(private val binding: ItemHistoryRowBinding) : RecyclerView.ViewHolder(binding.root) {
        var llHistoryItemMain= binding.llHistoryItemMain
        var tvPosition = binding.tvPosition
        var tvItem = binding.tvItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryRowBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPosition.text = (position+1).toString()
        holder.tvItem.text = datalist[position]
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

}