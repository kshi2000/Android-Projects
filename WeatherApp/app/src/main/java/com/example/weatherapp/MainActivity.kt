package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherService
import com.google.android.gms.location.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var customProgressBar: Dialog? = null
    private lateinit var mSharedPreferences: SharedPreferences
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isPermissionDenied: Boolean = false
            permissions.entries.forEach {
                if (!it.value) {
                    isPermissionDenied = true
                }
            }

            if (isPermissionDenied) {
                Toast.makeText(
                    this,
                    "You must give permissions, for app to work",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                requestLocationData()
            }
        }

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUI()

        if (!isLocationEnabled()) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showPermissionRationaleDialog()
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_refresh->{
                requestLocationData()
                true
            }
             else->{
                  super.onOptionsItemSelected(item)
             }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this).setTitle("You have not given permission")
            .setMessage("For the app to continue working, you must give all permissions")
            .setCancelable(false).setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Go to Settings") { _, _ ->

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.numUpdates = 1
        mLocationRequest.interval = 1000
        mLocationRequest.priority = android.location.LocationRequest.QUALITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            val data = location.lastLocation
            val lat = data!!.latitude
            val lng = data!!.longitude
            Log.i("Lat + lng ", "$lat  $lng")
            getWeatherStatusOfLocation(lat, lng)
        }
    }

    private fun getWeatherStatusOfLocation(latitude: Double, longitude: Double) {
        if (Constants.isNetworkConnected(this@MainActivity)) {
            val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

            val service = retrofit.create(WeatherService::class.java)
            val listCall = service.getWeather(latitude, longitude, Constants.WEATHER_API_KEY)

            showProgressBar()
            listCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        hideProgressBar()
                        val weatherList = response.body()
                        val responseInString = Gson().toJson(weatherList)
                        val editor =  mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA,responseInString)
                        editor.apply()
                        setupUI()
                        Log.i("Response success ", weatherList.toString())
                    } else {
                        Log.i("error ", response.toString())
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    hideProgressBar()
                    Log.i("Failed ", t.message.toString())
                }

            })
        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showProgressBar() {
        customProgressBar = Dialog(this)
        customProgressBar!!.setContentView(R.layout.dialog_loader)
        customProgressBar!!.show()
    }

    private fun hideProgressBar() {
        if (customProgressBar != null) {
            customProgressBar!!.dismiss()
        }
    }

    private fun setupUI() {

        // For loop to get the required data. And all are populated in the UI.
        val responseString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA,"")
        Log.i("res string", responseString.toString())
        if(!responseString.isNullOrEmpty()){
            val weatherList = Gson().fromJson(responseString,WeatherResponse::class.java)
            Log.i("weather list", weatherList.toString())
            for (z in weatherList.weather.indices) {
                Log.i("NAMEEEEEEEE", weatherList.weather[z].main)

                binding?.tvMain?.text = weatherList.weather[z].main
                binding?.tvMainDescription?.text = weatherList.weather[z].description
                binding?.tvTemp?.text =
                    weatherList.main.temp.toString()
                binding?.tvHumidity?.text = weatherList.main.humidity.toString() + " per cent"
                binding?.tvMin?.text = weatherList.main.temp_min.toString() + " min"
                binding?.tvMax?.text = weatherList.main.temp_max.toString() + " max"
                binding?.tvSpeed?.text = weatherList.wind.speed.toString()
                binding?.tvName?.text = weatherList.name
                binding?.tvCountry?.text = weatherList.sys.country
                binding?.tvSunriseTime?.text = unixTime(weatherList.sys.sunrise.toLong())
                binding?.tvSunsetTime?.text = unixTime(weatherList.sys.sunset.toLong())

                // Here we update the main icon
                when (weatherList.weather[z].icon) {
                    "01d" -> binding?.ivMain?.setImageResource(R.drawable.sunny)
                    "02d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "03d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "04d" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "04n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "10d" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                    "11d" -> binding?.ivMain?.setImageResource(R.drawable.storm)
                    "13d" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
                    "01n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "02n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "03n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "10n" -> binding?.ivMain?.setImageResource(R.drawable.cloud)
                    "11n" -> binding?.ivMain?.setImageResource(R.drawable.rain)
                    "13n" -> binding?.ivMain?.setImageResource(R.drawable.snowflake)
                }
            }
        }

    }

    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat") val sdf =
            SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}