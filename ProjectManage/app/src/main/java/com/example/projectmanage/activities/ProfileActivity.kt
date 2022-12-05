package com.example.projectmanage.activities

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.databinding.ActivityProfileBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileActivity : BaseActivity() {

    private var mImageUri: Uri? = null
    private var mFirebaseDownloadableUrl: String? = null

    private var mUserDetails: User? = null

    private var binding: ActivityProfileBinding? = null

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imgUri ->

            mImageUri = imgUri
            Log.i("image uri", mImageUri.toString())
            Glide.with(this).load(mImageUri).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding?.ivUserImage as ImageView)
        }

    private val externalStorageLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                galleryResultLauncher.launch("image/*")
            } else {
                Toast.makeText(
                    this@ProfileActivity,
                    "You must give permission for reading storage!!",
                    Toast.LENGTH_LONG
                )
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpToolbar()

        FirestoreClass().getUserDocument(this)

        binding?.ivUserImage?.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                galleryResultLauncher.launch("image/*")
            } else {
                externalStorageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        binding?.btnUpdate?.setOnClickListener {
            uploadUserImage()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding?.toolbarMyProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        binding?.toolbarMyProfileActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun loadProfile(info: User) {
        mUserDetails = info
        mFirebaseDownloadableUrl = info.image
        Log.i("Loaded img url ", mFirebaseDownloadableUrl!!)
        binding?.etEmail?.setText(info.email)
        binding?.etName?.setText(info.name)
        Glide.with(this).load(mFirebaseDownloadableUrl).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding?.ivUserImage as ImageView)
        if (info.mobile != 0L) {
            binding?.etMobile?.setText(info.mobile.toString())
        }
    }

    private fun updateUserDetails() {
        val userHashMap = HashMap<String, Any>()
        Log.i("Inside function update ", mFirebaseDownloadableUrl!!)
        userHashMap[Constants.IMAGE] = mFirebaseDownloadableUrl!!
        Log.i("To be updated ", mFirebaseDownloadableUrl!!)
        userHashMap[Constants.MOBILE] = binding?.etMobile?.text.toString().toLong()
        userHashMap[Constants.NAME] = binding?.etName?.text!!.toString()
        FirestoreClass().updateUser(this@ProfileActivity, userHashMap)
    }

    private fun uploadUserImage() {

        if (mImageUri != null) {
            showProgressBar()
            val storageRef = Firebase.storage.reference.child(
                "USER_IMAGE_${getUserId()}.${
                    Constants.getFileTypeFromUri(
                        this, mImageUri!!
                    )
                }"
            )

            storageRef.putFile(mImageUri!!).addOnSuccessListener { task ->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener { it ->
                    mFirebaseDownloadableUrl = it.toString()
                    Log.i("Downloadable img url ", mFirebaseDownloadableUrl!!)
                }
                updateUserDetails()
            }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    hideProgressBar()
                }

        } else {
            updateUserDetails()
        }
    }

    fun showUpdateSuccess() {
        hideProgressBar()
        setResult(Activity.RESULT_OK)
        finish()
    }
}