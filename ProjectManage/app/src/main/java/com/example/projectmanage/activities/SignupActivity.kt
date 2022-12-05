package com.example.projectmanage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ActivitySignupBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.User
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : BaseActivity() {

    private var binding:ActivitySignupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpToolbar()
        binding?.btnSignUp?.setOnClickListener{
            registerUser()
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding?.toolbarSignUpActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding?.toolbarSignUpActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun registerUser(){
        if(isFormInputsValid()){
            showProgressBar()

            //registers user in the auth module
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding?.etEmail?.text.toString(),binding?.etPassword?.text!!.toString()).addOnCompleteListener {
                task->
                hideProgressBar()
                if(task.isSuccessful){
                    val userDetails = task.result.user
                    FirestoreClass().registerUser(this, User(userDetails!!.uid,binding?.etName?.text!!.toString(),
                        userDetails!!.email!!
                    ))
                }
                else{
                    Toast.makeText(this@SignupActivity,task.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun isFormInputsValid():Boolean{
        val emailInput = binding?.etEmail?.text?.trim()
        val nameInput = binding?.etName?.text?.trim()
        val passwordInput = binding?.etPassword?.text?.trim()

        if(TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(nameInput) || TextUtils.isEmpty(passwordInput)){
            showErrorSnackBar("Please fill up all the form details")
            return false
        }
        return true
    }

    fun userRegisterSuccess() {
        Toast.makeText(this@SignupActivity,"${binding?.etName?.text}, you have registered with email",Toast.LENGTH_LONG).show()
        FirebaseAuth.getInstance().signOut()
    }
}