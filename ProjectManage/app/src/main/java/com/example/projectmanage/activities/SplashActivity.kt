package com.example.projectmanage.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ActivitySplashBinding
import com.example.projectmanage.firebase.FirestoreClass

class SplashActivity : AppCompatActivity() {

    private var binding:ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if(FirestoreClass().getUserId()!=""){
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                else{
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
                finish()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}