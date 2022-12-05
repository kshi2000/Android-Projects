package com.example.myquizapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat

class QuizQuestions : AppCompatActivity(), View.OnClickListener {

    private var mCurrentQuestion:Int = 1
    private var mQuestions:ArrayList<Question>? = null
    private var mSelectedOption:Int = 0
    private var mUserName:String? = null
    private var mScore:Int = 0
    private var answerDisplayed:Boolean = false

    private var ivImage : ImageView? = null
    private  var progressBar:ProgressBar? = null
    private var tvProgress:TextView? = null
    private var optionOne:TextView? = null
    private var optionTwo:TextView? = null
    private var optionThree:TextView? = null
    private var optionFour:TextView? = null
    private  var submitBtn:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_questions)
        mQuestions = Constants.getQuestions()
        mUserName = intent.getStringExtra(Constants.USER_NAME)

        ivImage = findViewById(R.id.iv_image)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        optionOne = findViewById(R.id.optionOne)
        optionTwo = findViewById(R.id.optionTwo)
        optionThree = findViewById(R.id.optionThree)
        optionFour = findViewById(R.id.optionFour)
        submitBtn = findViewById(R.id.submitButton)

        optionOne?.setOnClickListener(this)
        optionTwo?.setOnClickListener(this)
        optionThree?.setOnClickListener(this)
        optionFour?.setOnClickListener(this)
        submitBtn?.setOnClickListener(this)

        setQuestion()
        setDefaultOptionsView()
    }

    private fun setQuestion() {
          ivImage?.setImageResource(mQuestions!![mCurrentQuestion-1].Image)
        progressBar?.progress = mCurrentQuestion
        tvProgress?.text = "${mCurrentQuestion}/${mQuestions!!.size}"
        optionOne?.text = mQuestions!![mCurrentQuestion-1].optionOne
        optionTwo?.text = mQuestions!![mCurrentQuestion-1].optionTwo
        optionThree?.text = mQuestions!![mCurrentQuestion-1].optionThree
        optionFour?.text = mQuestions!![mCurrentQuestion-1].optionFour

    }

    private fun setDefaultOptionsView(){
        val allOptionsList = ArrayList<TextView>()

        optionOne?.let { allOptionsList.add(it) }
        optionTwo?.let { allOptionsList.add(it) }
        optionThree?.let { allOptionsList.add(it) }
        optionFour?.let { allOptionsList.add(it) }

        for(option in allOptionsList){
            option.typeface = Typeface.DEFAULT
            option.setTextColor(Color.parseColor(  "#7A8089"))
            option.background = ContextCompat.getDrawable(this,R.drawable.default_option_border_bg)
        }
    }

    private fun setSelectedOption(tv:TextView,selectedOption:Int){
        setDefaultOptionsView()
        mSelectedOption = selectedOption

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.typeface = Typeface.DEFAULT_BOLD
        tv.background = ContextCompat.getDrawable(this,R.drawable.seleted_option_border_bg)
    }

   private fun highlightAnswer(answer:Int,isCorrect:Boolean){
     var drawable:Int? = null
       drawable = if(isCorrect) {
           R.drawable.correct_option_bg
       } else{
           R.drawable.wrong_option_bg
       }
       when(answer){
           0->{
               optionOne?.background = ContextCompat.getDrawable(this,drawable)
           }
           1->{
               optionTwo?.background = ContextCompat.getDrawable(this,drawable)
           }
           2->{
               optionThree?.background = ContextCompat.getDrawable(this,drawable)
           }
           3->{
               optionFour?.background = ContextCompat.getDrawable(this,drawable)
           }
       }
   }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.optionOne-> {
                optionOne?.let { setSelectedOption(it,0) }
            }
            R.id.optionTwo-> {
                optionTwo?.let { setSelectedOption(it,1) }
            }
            R.id.optionThree-> {
                optionThree?.let { setSelectedOption(it,2) }
            }
            R.id.optionFour-> {
                optionFour?.let { setSelectedOption(it,3) }
            }

            R.id.submitButton-> {
                if (answerDisplayed) {
                    if (mCurrentQuestion != mQuestions!!.size) {
                        submitBtn?.text = "SUBMIT"
                    }
                    mSelectedOption = 0
                    mCurrentQuestion++
                    answerDisplayed = false
                    if(mCurrentQuestion > mQuestions!!.size){
                        val intent = Intent(this,ResultActivity::class.java)
                        intent.putExtra(Constants.USER_NAME,mUserName)
                        intent.putExtra(Constants.TOTAL_QUESTIONS,mQuestions!!.size)
                        intent.putExtra(Constants.CORRECT_ANSWERS,mScore)
                        startActivity(intent)
                    }
                    else{
                        setQuestion()
                        setDefaultOptionsView()
                    }

                } else {
                    highlightAnswer(mQuestions!![mCurrentQuestion - 1].correctOption, true)
                    mScore++
                    if (mSelectedOption != mQuestions!![mCurrentQuestion - 1].correctOption) {
                        mScore--
                        highlightAnswer(mSelectedOption, false)
                    }
                    answerDisplayed = true
                    if (mCurrentQuestion != mQuestions?.size) {
                        submitBtn?.text = "GO TO NEXT QUESTION"
                    } else {
                        submitBtn?.text = "FINISH"
                    }

                }

            }
        }
    }
}