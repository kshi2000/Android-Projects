package com.example.projectmanage.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.projectmanage.R
import com.example.projectmanage.adapters.BoardItemsAdapter
import com.example.projectmanage.databinding.ActivityMainBinding
import com.example.projectmanage.databinding.NavHeaderMainBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.Board
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null

    private lateinit var mUsername:String
    private lateinit var mSharedPreferences: SharedPreferences

    private val profileUpdateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){  it->

        if(it.resultCode == Activity.RESULT_OK){
            FirestoreClass().getUserDocument(this)
        }
        else{
            Log.i("cancelled ",it.toString())
        }
    }

    private val createBoardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){  it->

        if(it.resultCode == Activity.RESULT_OK){
            FirestoreClass().getBoardDocuments(this)
        }
        else{
            Log.i("cancelled ",it.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupActionBar()
        binding?.navView?.setNavigationItemSelectedListener(this)


        mSharedPreferences = getSharedPreferences(Constants.PROJECT_MANAGE_PREFERENCES,Context.MODE_PRIVATE)

        val tokenUpdatedInDb = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED,false)
        if(tokenUpdatedInDb){
            FirestoreClass().getUserDocument(this)
        }
        else{
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener(this@MainActivity) {
                    updateFcmToken(it)
                }
        }
        FirestoreClass().getBoardDocuments(this)

        binding?.appBarMain?.fabCreateBoard?.setOnClickListener{
            val intent = Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUsername)
            createBoardLauncher.launch(intent)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding?.appBarMain?.toolbarMainActivity)
        binding?.appBarMain?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_baseline_dehaze_24)
        binding?.appBarMain?.toolbarMainActivity?.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    override fun onBackPressed() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            doublePressToExit()
        }
    }

    private fun toggleDrawer() {
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                profileUpdateLauncher.launch(Intent(this@MainActivity,ProfileActivity::class.java))
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateUserNavDetails(info: User) {

        mUsername = info.name

        val headerView = binding?.navView?.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView!!)
        Glide.with(this).load(info.image).centerCrop().placeholder(R.drawable.ic_user_place_holder)
            .into(headerBinding.ivUserImage)
        headerBinding.tvUsername.text = info.name
    }

    fun populateBoardsUi(list:ArrayList<Board>){
        if(list.size>0){
            binding?.appBarMain?.contentMain?.rvBoardsList?.visibility = View.VISIBLE
            binding?.appBarMain?.contentMain?.tvNoBoardsAvailable?.visibility = View.GONE
            val adapter = BoardItemsAdapter(this,list)
            adapter.clickListener = object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            }
            binding?.appBarMain?.contentMain?.rvBoardsList?.adapter = adapter
            binding?.appBarMain?.contentMain?.rvBoardsList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            binding?.appBarMain?.contentMain?.rvBoardsList?.setHasFixedSize(true)
        }
        else{
            binding?.appBarMain?.contentMain?.rvBoardsList?.visibility = View.GONE
            binding?.appBarMain?.contentMain?.tvNoBoardsAvailable?.visibility = View.VISIBLE
        }
    }

    fun tokenUpdatedSuccess() {
       val editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED,true)
        editor.apply()
        FirestoreClass().getUserDocument(this)
    }

    private fun updateFcmToken(token:String){
        val hashmap = HashMap<String,Any>()
        hashmap[Constants.FCM_TOKEN] = token
        FirestoreClass().updateUser(this,hashmap)
    }
}