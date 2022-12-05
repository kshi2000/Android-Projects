package com.example.projectmanage.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ActivitySigninBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninActivity : BaseActivity() {

    private var binding: ActivitySigninBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpToolbar()
        binding?.btnSignIn?.setOnClickListener{
            loginUser()
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding?.toolbarSignInActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding?.toolbarSignInActivity?.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    private fun loginUser(){
        if(isFormInputsValid()){
            showProgressBar()

            //signing user in
            auth.signInWithEmailAndPassword(binding?.etEmail?.text!!.toString(), binding?.etPassword?.text!!.toString())
                .addOnCompleteListener(this) { task ->
                    hideProgressBar()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        FirestoreClass().getUserDocument(this)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(baseContext, "Login failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun isFormInputsValid():Boolean{
        val emailInput = binding?.etEmail?.text?.trim()
        val passwordInput = binding?.etPassword?.text?.trim()

        if(TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput)){
            showErrorSnackBar("Please fill up all the form details")
            return false
        }
        return true
    }

    fun signinSuccess(user: User) {
        startActivity(Intent(this@SigninActivity,MainActivity::class.java))
        finish()
    }

}