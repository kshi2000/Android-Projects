package com.example.sevenminuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sevenminuteworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding:ActivityHistoryBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(binding?.toolbarHistory)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarHistory?.setNavigationOnClickListener {
            onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        getAllDates(historyDao)

    }

    private fun getAllDates(historyDao: HistoryDao){
        lifecycleScope.launch{
            historyDao.fetchAllDates().collect(){  allDates->
                if(allDates.isNotEmpty()){
                    binding?.tvHistory?.visibility = View.VISIBLE
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoDataAvailable?.visibility = View.GONE

                    // Creates a vertical Layout Manager
                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)

                    val datesPassedToAdapter = ArrayList<String>()
                    for(date in allDates){
                        datesPassedToAdapter.add(date.date)
                    }

                    val historyAdapter = HistoryAdapter(datesPassedToAdapter)

                    binding?.rvHistory?.adapter = historyAdapter
                }
                else{
                    binding?.tvHistory?.visibility = View.GONE
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoDataAvailable?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}