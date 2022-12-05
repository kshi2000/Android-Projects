package com.example.myquizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private var tvText : TextInputEditText? = null
    private var submitBtn:Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvText = findViewById(R.id.enteredText)
       submitBtn = findViewById(R.id.submitButton)

        submitBtn?.setOnClickListener {
            if (tvText?.text?.isEmpty() == true) {
                Toast.makeText(this, "Please enter name", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, QuizQuestions::class.java)
                intent.putExtra(Constants.USER_NAME,tvText?.text.toString())

                startActivity(intent)
                finish()
            }
        }
    }
}