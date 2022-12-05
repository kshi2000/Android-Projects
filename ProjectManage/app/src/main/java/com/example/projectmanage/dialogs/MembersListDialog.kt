package com.example.projectmanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.R
import com.example.projectmanage.adapters.MemberItemAdapter
import com.example.projectmanage.models.User

abstract class MembersListDialog(context: Context,var title:String="",var list:ArrayList<User>):Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

     private fun setUpRecyclerView(view: View){
         val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
         val rvList = view.findViewById<RecyclerView>(R.id.rvList)

         tvTitle.setText(title)
         if(list.size>0){
             rvList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
             rvList.setHasFixedSize(true)
             val adapter =  MemberItemAdapter(context,list)
             rvList.adapter = adapter
             adapter.setOnClickListener(object : MemberItemAdapter.OnClickListener{
                 override fun onClick(position: Int, user: User, action: String) {
                     dismiss()
                     onItemSelected(user,action)
                 }

             })

         }

     }

    abstract fun onItemSelected(user: User, action: String)
}