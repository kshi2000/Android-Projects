package com.example.happyplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(),OnMapReadyCallback {
    // TODO (Step 1: Create a variable for data model class.)
    // START
    private var mHappyPlaceDetails: HappyPlaceEntity? = null
    // END
    private var binding: ActivityMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // TODO (Step 2: Receives the details through intent and used further.)
        // START
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)) {
            mHappyPlaceDetails =
                intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL) as HappyPlaceEntity?
        }

        if (mHappyPlaceDetails != null) {

            setSupportActionBar(binding?.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mHappyPlaceDetails!!.title

            binding?.toolbarMap?.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
        // END
    }

    // TODO (Step 4: After extending an interface adding the location pin to the map when the map is ready using the latitude and longitude.)
    // START
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        /**
         * Add a marker on the location using the latitude and longitude and move the camera to it.
         */
        val position = LatLng(
            mHappyPlaceDetails!!.latitude,
            mHappyPlaceDetails!!.longitude
        )
        googleMap.addMarker(MarkerOptions().position(position).title(mHappyPlaceDetails!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 10f)
        googleMap.animateCamera(newLatLngZoom)
    }
    // END
}