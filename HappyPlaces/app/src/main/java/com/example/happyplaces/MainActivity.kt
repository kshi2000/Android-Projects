package com.example.happyplaces

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.utils.SwipeToDeleteCallback
import com.example.happyplaces.utils.SwipeToEditCallback
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding:ActivityMainBinding? = null

    private var happyPlaceDao:HappyPlaceDao? = null

    private val addHappyPlaceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            renderHappyPlaces(happyPlaceDao!!)
        }
        else{
            Log.i("not inserted","back pressed maybe")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        happyPlaceDao = (application as HappyPlaceApp).db.happyPlaceDao()

        renderHappyPlaces(happyPlaceDao!!)

        binding?.fabAddHappyPlace?.setOnClickListener{
            val intent = Intent(this,AddHappyPlaceActivity::class.java)
            addHappyPlaceLauncher.launch(intent)
        }
    }

    private  fun renderHappyPlaces(happyPlaceDao: HappyPlaceDao){
        lifecycleScope.launch{
            happyPlaceDao.fetchAllPlace().collect{
                if(it.isNotEmpty()){
                    binding?.tvNoRecordsAvailable?.visibility = View.GONE
                    binding?.rvHappyPlacesList?.visibility = View.VISIBLE
                    setupHappyPlacesRecyclerView(it)
                }
                else{
                    binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
                    binding?.rvHappyPlacesList?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupHappyPlacesRecyclerView(dataList: List<HappyPlaceEntity>) {

        binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
        binding?.rvHappyPlacesList?.setHasFixedSize(true)

      val happyPlaceAdapter =   HappyPlaceAdapter(this,dataList)
        binding?.rvHappyPlacesList?.adapter = happyPlaceAdapter

        happyPlaceAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int, item: HappyPlaceEntity) {
                var intent = Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAIL,item)
                startActivity(intent)
            }
        })


        val editSwipeHandler = object: SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = (binding?.rvHappyPlacesList?.adapter) as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition,
                    ADD_PLACE_REQUEST_CODE)
            }
        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)

        val deleteSwipeHandler = object: SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = (binding?.rvHappyPlacesList?.adapter) as HappyPlaceAdapter
                lifecycleScope.launch {
                    happyPlaceDao!!.delete(adapter.datalist[viewHolder.adapterPosition])
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }
        }

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(binding?.rvHappyPlacesList)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // check if the request code is same as what is passed  here it is 'ADD_PLACE_ACTIVITY_REQUEST_CODE'
        if (requestCode == ADD_PLACE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                renderHappyPlaces(happyPlaceDao!!)
            } else {
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }

    companion object{
        const val ADD_PLACE_REQUEST_CODE = 1
       var EXTRA_PLACE_DETAIL = "extra_place_details"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}