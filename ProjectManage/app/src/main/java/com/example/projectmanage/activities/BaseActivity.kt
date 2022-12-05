package com.example.projectmanage.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.projectmanage.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

open class BaseActivity : AppCompatActivity() {

    private var mCustomProgress:Dialog? = null
    private var isBackBtnClickedOnce:Boolean = false
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    fun showProgressBar(){
        mCustomProgress = Dialog(this)
        mCustomProgress!!.setContentView(R.layout.dialog_custom_progress)
        mCustomProgress!!.show()
    }

    fun hideProgressBar(){
        if(mCustomProgress!=null){
            mCustomProgress!!.dismiss()
        }
    }

    fun getUserId():String{
       return auth.currentUser!!.uid
    }

    fun doublePressToExit(){
        if(isBackBtnClickedOnce){
            onBackPressed()
            return
        }
        this.isBackBtnClickedOnce = true
        Timer().schedule(3000){
           this@BaseActivity.isBackBtnClickedOnce= false
        }

        Toast.makeText(this,R.string.please_click_back_again_to_exit,Toast.LENGTH_LONG).show()
    }

    fun showErrorSnackBar(msg:String){
        val snackbar = Snackbar.make(findViewById( android.R.id.content),msg,Snackbar.LENGTH_LONG)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(this,R.color.errorColor))
        snackbar.show()
    }
}