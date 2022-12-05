package com.example.projectmanage.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.adapters.BoardItemsAdapter
import com.example.projectmanage.databinding.ActivityCreateBoardBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.Board
import com.example.projectmanage.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CreateBoardActivity : BaseActivity() {

    private var binding: ActivityCreateBoardBinding? = null

    private var mImageUri: Uri? = null
    private var mFirebaseDownloadableUrl: String = ""

    private lateinit var mUserName: String

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imgUri ->

            if (imgUri != null && imgUri.toString().isNotEmpty()) {
                mImageUri = imgUri
                Glide.with(this).load(mImageUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding?.ivBoardImage as ImageView)
            }

        }

    private val externalStorageLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) {
                galleryResultLauncher.launch("image/*")
            } else {
                Toast.makeText(
                    this@CreateBoardActivity, "You must give permission for reading storage!!",
                    Toast.LENGTH_LONG
                )
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME)!!
        }

        binding?.ivBoardImage?.setOnClickListener {
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

        binding?.btnCreate?.setOnClickListener {
            if(binding?.etBoardName?.text!!.isNotEmpty())
            {
                uploadUserImage()
            }
            else{
                showErrorSnackBar("Please enter board name")
            }
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding?.toolbarCreateBoardActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        }

        binding?.toolbarCreateBoardActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    private fun createBoard() {
        val assignedUsers = ArrayList<String>()
        assignedUsers.add(getUserId())
        Log.i("img url ",mFirebaseDownloadableUrl)
            FirestoreClass().createBoard(
                this,
                Board(
                    binding?.etBoardName?.text.toString(),
                    mFirebaseDownloadableUrl!!,
                    mUserName,
                    assignedUsers
                )
            )
    }

    private fun uploadUserImage(){
        showProgressBar()
        if(mImageUri!=null){
            showProgressBar()
            val storageRef = Firebase.storage.reference.child("BOARD_IMAGE_${System.currentTimeMillis()}.${Constants.getFileTypeFromUri(
                this,mImageUri!!
            )}")

            storageRef.putFile(mImageUri!!).addOnSuccessListener{ task->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener { it->
                    mFirebaseDownloadableUrl = it.toString()
                }
                createBoard()
            }
                .addOnFailureListener{
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                    hideProgressBar()
                }

        }
        else{
            createBoard()
        }
    }

    fun createdBoardSuccess() {
        hideProgressBar()
        setResult(RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}