package com.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultActivity : AppCompatActivity() {
    private var tvUserName:TextView? = null
    private var tvScoreStatement:TextView? = null
    private var btnFinish:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        tvUserName = findViewById(R.id.user_name)
        tvScoreStatement  = findViewById(R.id.score_statement)
        btnFinish = findViewById(R.id.finish_btn)

        tvUserName?.text = intent.getStringExtra(Constants.USER_NAME)
        tvScoreStatement?.text = "Your score is ${intent.getIntExtra(Constants.CORRECT_ANSWERS,0)} out of ${intent.getIntExtra(Constants.TOTAL_QUESTIONS,0)}"

        btnFinish?.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}