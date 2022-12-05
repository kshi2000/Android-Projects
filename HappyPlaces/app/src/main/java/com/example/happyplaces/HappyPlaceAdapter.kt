package com.example.happyplaces

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ItemHappyPlaceBinding

class HappyPlaceAdapter(private val context: Context, var datalist:List<HappyPlaceEntity>) :
    RecyclerView.Adapter<HappyPlaceAdapter.ItemHolder>() {

   private  var onClickListener:OnClickListener? = null

    class ItemHolder(val binding:ItemHappyPlaceBinding):RecyclerView.ViewHolder(binding.root) {
        val tvTitle = binding?.tvTitle
        val tvDescription = binding?.tvDescription
    }

    fun notifyEditItem(activity: Activity,position: Int,requestCode:Int){
        val intent = Intent(context,AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL,datalist[position])
        activity.startActivityForResult(intent,requestCode)
       this.notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HappyPlaceAdapter.ItemHolder {
        val binding = ItemHappyPlaceBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }


    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val entity = datalist[position]

        holder.tvTitle?.text = entity.title
        holder?.tvDescription?.text = entity.description

        holder?.binding?.root?.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position,entity)
            }
        }
    }

    override fun getItemCount(): Int {
        return  datalist.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position:Int,item:HappyPlaceEntity)
    }


}