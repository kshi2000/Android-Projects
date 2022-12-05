package com.example.firstandroidapplication

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

     private var tvDisplayDate:TextView? = null
    private var tvDisplayMinutes:TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val btnDatePicker:Button = findViewById(R.id.btnDatePicker)
        tvDisplayDate = findViewById(R.id.displayDate)
        tvDisplayMinutes = findViewById(R.id.displayMinutes)
        btnDatePicker.setOnClickListener{
            clickedDateBtn()
        }

    }

    private fun clickedDateBtn(){
        val myCalender = Calendar.getInstance()
        val year = myCalender.get(Calendar.YEAR)
        val month = myCalender.get(Calendar.MONTH)
        val day = myCalender.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this,{_,selectedYear,selectedMonth,selectedDayOfYear->
            val selectedDate = "${selectedDayOfYear}/${selectedMonth+1}/${selectedYear}"

            tvDisplayDate?.text = selectedDate
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(selectedDate)
            val dateInMs = formattedDate.time

            val currentDateInMs = System.currentTimeMillis()

            val diffInMinutes = (currentDateInMs-dateInMs)/60000

            tvDisplayMinutes?.text = diffInMinutes.toString()

        },year,month,day)

        dpd.datePicker.maxDate = System.currentTimeMillis()
        dpd.show()

    }

}