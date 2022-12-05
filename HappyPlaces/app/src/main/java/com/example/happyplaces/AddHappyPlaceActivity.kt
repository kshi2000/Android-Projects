package com.example.happyplaces

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.location.LocationRequest
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null

    private var imageUri: Uri? = null

    private var mHappyPlace:HappyPlaceEntity? = null
    private var mLongitude:Double? = null
    private var mLatitude:Double? = null

    private lateinit var mFuseLocationClient:FusedLocationProviderClient

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                Log.i("Gallery uri", uri.toString())
                binding?.ivPlaceImage?.setImageURI(uri)
            }
        }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->

            if (isSuccess) {
                Log.i("Camera img uri", imageUri.toString())
                binding?.ivPlaceImage?.setImageURI(null)
                binding?.ivPlaceImage?.setImageURI(imageUri)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAIL)){
            mHappyPlace = intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAIL)
            Log.i("Happy place",mHappyPlace.toString())
        }

        if(mHappyPlace!=null){
            supportActionBar?.title = "Edit Happy Place"

            binding?.etDate?.setText(mHappyPlace!!.date)
            binding?.etDescription?.setText(mHappyPlace!!.description)
            binding?.etTitle?.setText(mHappyPlace!!.title)
            binding?.etLocation?.setText(mHappyPlace!!.location)
            binding?.btnSave?.text = "Update"

            mLongitude = mHappyPlace!!.longitude
            mLatitude = mHappyPlace!!.latitude
        }

        if(!Places.isInitialized()){
            Places.initialize(this@AddHappyPlaceActivity,resources.getString(R.string.google_places_api_key))
        }

        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
        binding?.etLocation?.setOnClickListener(this)
        binding?.btnCurrentLocation?.setOnClickListener(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK)
        {
            if(requestCode == ADD_LOCATION_REQUEST_CODE){
                val place:Place = Autocomplete.getPlaceFromIntent(data)
                binding?.etLocation?.setText(place.address)
                mLongitude = place.latLng.longitude
                mLatitude = place.latLng.latitude
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding?.etDate?.id -> {
                val calendar = Calendar.getInstance()

                val dpd = DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val date = sdf.format(calendar.time)
                        binding?.etDate?.setText(date)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                dpd.show()
            }

            binding?.tvAddImage?.id -> {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Requesting permissions")
                alertDialogBuilder.setItems(
                    arrayOf(
                        "Set image from device",
                        "Set image from camera"
                    )
                ) { _, index ->

                    when (index) {
                        0 -> {
                            choosePhotoFromGallery()
                        }

                        1 -> {
                            takePhotoFromCamera()
                        }
                    }
                }.show()
            }

            binding?.btnSave?.id -> {

                if (binding?.etDate?.text?.isEmpty() == true || binding?.etDescription?.text?.isEmpty() == true || binding?.etTitle?.text?.isEmpty() == true || binding?.etLocation?.text?.isEmpty() == true) {
                    Toast.makeText(this, "Please fill out all details", Toast.LENGTH_LONG)
                } else {
                    val happyPlaceDao = (application as HappyPlaceApp).db.happyPlaceDao()
                    GlobalScope.launch {
                        if(mHappyPlace == null){
                            happyPlaceDao.insert(
                                HappyPlaceEntity(
                                    0,
                                    binding?.etTitle?.text?.toString()!!,
                                    binding?.etDescription?.text?.toString()!!,
                                    binding?.etDate?.text?.toString()!!,
                                    binding?.etLocation?.text?.toString()!!,
                                    mLatitude!!,
                                    mLongitude!!
                                )
                            )
                        }
                        else{
                            happyPlaceDao.update(
                                mHappyPlace!!.id,
                                binding?.etTitle?.text?.toString()!!,
                                binding?.etDescription?.text?.toString()!!,
                                binding?.etDate?.text?.toString()!!,
                                binding?.etLocation?.text?.toString()!!,
                                mLatitude!!,
                                mLongitude!!
                            )
                        }

                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }

            binding?.etLocation?.id -> {
                try {
                    val fields = listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS)
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).build(this)
                    startActivityForResult(intent, ADD_LOCATION_REQUEST_CODE)
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

            }

            binding?.btnCurrentLocation?.id ->{
                if(!isLocationEnabled()){
                    Toast.makeText(this,"Location is off, please on it",Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                else{
                    Dexter.withContext(this)
                        .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ).withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                if (report.areAllPermissionsGranted()) {
                                    requestUserLocation()
                                }
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permissions: List<PermissionRequest?>?,
                                token: PermissionToken?
                            ) {
                                showPermissionRationale()
                            }
                        }).onSameThread().check()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestUserLocation() {
        var fuseLocationOptions = com.google.android.gms.location.LocationRequest()
        fuseLocationOptions.interval = 0
        fuseLocationOptions.numUpdates=1
        fuseLocationOptions.priority = LocationRequest.QUALITY_BALANCED_POWER_ACCURACY

        mFuseLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mFuseLocationClient.requestLocationUpdates(fuseLocationOptions,mLocationCallback, Looper.myLooper())
    }

    private  val mLocationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
           val mLastLocation = locationResult.lastLocation
            mLatitude = mLastLocation!!.latitude
            mLongitude = mLastLocation!!.longitude
            val addressTask=GetAddressFromLatLng(this@AddHappyPlaceActivity, mLatitude!!,
                mLongitude!!
            )
            addressTask.setCustomAddressListener(object : GetAddressFromLatLng.AddressListener {
                override fun onAddressFound(address: String) {
                    binding?.etLocation?.setText(address)
                }
                override fun onError() {
                    Log.e("Get address:: ", "onError: Something went wrong", )
                }

            })
            runBlocking {
                addressTask.launchBackgroundProcessForRequest()
            }
        }
        }


    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
       return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        imageUri = createImageUri()
                        cameraLauncher.launch(imageUri)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showPermissionRationale()
                }
            }).onSameThread().check()
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
//                        Toast.makeText(this@AddHappyPlaceActivity,"Cool!, now you can choose pic from gallery",Toast.LENGTH_LONG).show()
                        galleryLauncher.launch("image/*")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    showPermissionRationale()
                }
            }).onSameThread().check()
    }

    private fun showPermissionRationale() {
        var alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("You have not given permission")
        alertDialogBuilder.setMessage("You may choose to grant permission, by going to the settings app")

        alertDialogBuilder.setPositiveButton("Go to Settings") { _, _ ->

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun createImageUri(): Uri? {
        val imgFile = File(applicationContext.filesDir, UUID.randomUUID().toString())
        return FileProvider.getUriForFile(
            applicationContext,
            "com.example.happyplaces.fileProvider",
            imgFile
        )
    }

    companion object{
        var ADD_LOCATION_REQUEST_CODE = 1
    }

}