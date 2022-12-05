package com.example.myquizapp

data class Question(
    val id:Int,
    val question:String ,
    val Image:Int,
    val optionOne:String,
    val optionTwo:String,
    val optionThree:String,
    val optionFour:String,
    val correctOption:Int
)
