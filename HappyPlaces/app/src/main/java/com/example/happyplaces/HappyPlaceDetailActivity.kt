package com.example.happyplaces

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityHappyPlaceDetailBinding


class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var happyPlaceEntity:HappyPlaceEntity? = null

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
           happyPlaceEntity =  intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceEntity?
        }

        if(happyPlaceEntity!=null){
            setSupportActionBar(binding?.toolbarHappyPlaceDetail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceEntity.title

            binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener{
                onBackPressed()
            }

            binding?.tvDescription?.text = happyPlaceEntity.description
            binding?.tvLocation?.text = happyPlaceEntity.location
        }

        binding?.btnViewOnMap?.setOnClickListener {
            val intent = Intent(this@HappyPlaceDetailActivity, MapActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_PLACE_DETAIL, happyPlaceEntity)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}