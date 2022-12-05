package com.example.sevenminuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sevenminuteworkout.databinding.ActivityExcerciseBinding
import com.example.sevenminuteworkout.databinding.ConfirmationDialogBinding
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExcerciseBinding? = null
    private var itemsAdapter:ExerciseAdapter?=null

    private var timer: CountDownTimer? = null
    private var timeLeft = 2

    private var tts: TextToSpeech? = null

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExerciseIndex = -1

    private var mediaPlayer:MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.getExerciseList()
        currentExerciseIndex++

        setItemRecyclerView(exerciseList!!)

        tts = TextToSpeech(this,this)

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.tvTimer?.text = timeLeft.toString()

        startCountDown()

    }

    override fun onBackPressed() {
        var dialog = Dialog(this)
        var dialogBinding = ConfirmationDialogBinding.inflate(layoutInflater)

        dialogBinding?.root?.let { dialog.setContentView(it) }

        dialog.setCanceledOnTouchOutside(false)

        dialogBinding.btnNo.setOnClickListener(){
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener{
            this@ExerciseActivity.finish()
            dialog.dismiss()
        }

            dialog.show()
    }

    private fun setItemRecyclerView(list:ArrayList<ExerciseModel>){
        itemsAdapter  = ExerciseAdapter(list)
        binding?.rvItems?.adapter = itemsAdapter
        binding?.rvItems?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun setTimerAndText(timeInput: Int) {
        binding?.progressBar?.progress = timeInput
        binding?.tvTimer?.text = timeInput.toString()
    }

    private fun displayToast(text: String, time: Int) {
        Toast.makeText(this@ExerciseActivity, text, time).show()
    }

    private fun startCountDown() {
        timeLeft = 10

//        val soundUri = Uri.parse("android.resource://com.example.sevenminuteworkout/"+R.raw.sample)
        try {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.sample)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        binding?.progressBar?.max = 10
        binding?.tvGetReady?.text = "Get Ready"
        binding?.ivImage?.visibility = View.GONE
        binding?.tvTimer?.text = timeLeft.toString()

        binding?.tvNextExercise?.visibility =  View.VISIBLE
        binding?.tvUpcoming?.visibility = View.VISIBLE

        if(currentExerciseIndex < exerciseList!!.size) binding?.tvNextExercise?.text =  (exerciseList!![currentExerciseIndex].getName())
        else binding?.tvNextExercise?.text = "Nothing"


        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                setTimerAndText(--timeLeft)
            }

            override fun onFinish() {
                if (currentExerciseIndex != exerciseList!!.size) {
                    startExerciseCountDown()
                    currentExerciseIndex++
                    Log.i("Inside setup fn ",currentExerciseIndex.toString())
                } else {
                    displayToast("Countdown complete", LENGTH_LONG)
                }

            }

        }.start()

    }

    private fun startExerciseCountDown() {

        Log.i("Inside exercise fn ",currentExerciseIndex.toString())
        timeLeft = 30
        binding?.progressBar?.max = 30

        exerciseList!![currentExerciseIndex].setSelected(true)
        itemsAdapter?.notifyItemChanged(currentExerciseIndex)

        binding?.tvGetReady?.text = exerciseList!![currentExerciseIndex].getName()

        speakOut(binding?.tvGetReady?.text as String)

        binding?.ivImage?.setImageResource(exerciseList!![currentExerciseIndex].getImage())
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.tvNextExercise?.visibility = View.GONE
        binding?.tvUpcoming?.visibility = View.GONE

        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                setTimerAndText(--timeLeft)
            }

            override fun onFinish() {
                exerciseList!![currentExerciseIndex-1].setSelected(false)
                exerciseList!![currentExerciseIndex-1].setCompleted(true)
                itemsAdapter?.notifyItemChanged(currentExerciseIndex-1)
                if(currentExerciseIndex>exerciseList!!.size){
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    startCountDown()
                }

            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeLeft = 0
        timer?.cancel()
        binding = null
        mediaPlayer?.stop()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            }
        }
    }

    private fun speakOut(text:String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }
}