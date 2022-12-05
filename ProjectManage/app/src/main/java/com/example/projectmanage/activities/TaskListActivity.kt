package com.example.projectmanage.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmanage.R
import com.example.projectmanage.adapters.TaskListItemsAdapter
import com.example.projectmanage.databinding.ActivityTaskListBinding
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.Board
import com.example.projectmanage.models.Card
import com.example.projectmanage.models.Task
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import java.text.FieldPosition

class TaskListActivity : BaseActivity() {
    private var binding:ActivityTaskListBinding?=null

    private lateinit var mBoard: Board
    private lateinit var mBoardDocumentId:String
    lateinit var mBoardMemberDetails:ArrayList<User>

    private val updateIfMemberAddedLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            FirestoreClass().getBoardById(this,mBoardDocumentId)
        }
    }

    private val updateIfCardDeletedOrEdited = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            FirestoreClass().getBoardById(this,mBoardDocumentId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
            FirestoreClass().getBoardById(this,mBoardDocumentId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent = Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoard)
                updateIfMemberAddedLauncher.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }

    private fun setupActionBar(title:String) {
        setSupportActionBar(binding?.toolbarTaskListActivity)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

     fun setRecyclerView(board: Board){
            mBoard = board
            FirestoreClass().getBoardMembersDetails(this,mBoard.assignedTo)
        }

    fun moveToCardDetailsActivity(taskPosition: Int,cardPosition:Int){
        val intent = Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.TASK_LIST_POSITION,taskPosition)
        intent.putExtra(Constants.CARD_LIST_POSITION,cardPosition)
        intent.putExtra(Constants.BOARD_DETAILS,mBoard)
        intent.putExtra(Constants.BOARD_MEMBERS_DETAILS,mBoardMemberDetails)
        updateIfCardDeletedOrEdited.launch(intent)
    }

    fun createTask(taskName:String){
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        mBoard.taskList.add(Task(taskName,FirestoreClass().getUserId()))
        FirestoreClass().addUpdateBoardById(this,mBoard,Constants.ADD_TASK_CODE)
    }

    fun editTask(newTaskName: String,position: Int,oldTask: Task){
        val oldBoard  = mBoard
        val newTask = Task(newTaskName,oldTask.createdBy)
        mBoard.taskList[position] = newTask
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
        FirestoreClass().addUpdateBoardById(this,mBoard,Constants.EDIT_TASK_CODE,oldBoard,position)
    }

    fun deleteTask(position: Int){
        val oldBoard = mBoard
        mBoard.taskList.removeAt(position)
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
        FirestoreClass().addUpdateBoardById(this,mBoard,Constants.DELETE_TASK_CODE,oldBoard,position)
    }
    fun addedTaskFailureNotification(){
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        mBoard.taskList.add(Task("Title"))
    }

    fun addedTaskSuccessNotification() {
        mBoard.taskList.add(Task("Title"))
        binding?.rvTaskList?.adapter?.notifyItemRangeChanged(mBoard.taskList.size-2,2)
    }

    fun editedTaskSuccessNotification(position: Int?) {
        mBoard.taskList.add(Task("Title"))
        if(position!=null){
            binding?.rvTaskList?.adapter?.notifyItemChanged(position!!)
            binding?.rvTaskList?.adapter?.notifyItemInserted(mBoard.taskList.size)
        }
    }

    fun editedTaskFailureNotification(oldBoard: Board?) {
        mBoard = oldBoard!!
    }

    fun deletedTaskSuccessNotification(positionToBeChangedOrDeleted: Int?) {
        mBoard.taskList.add(Task("Title"))
        binding?.rvTaskList?.adapter?.notifyItemRemoved(positionToBeChangedOrDeleted!!)
        binding?.rvTaskList?.adapter?.notifyItemInserted(mBoard.taskList.size)
    }

    fun deletedTaskFailureNotification(oldBoard: Board?) {
        mBoard = oldBoard!!
    }

    fun createCard(taskPosition:Int,cardName:String){
        val oldBoard = mBoard
        mBoard.taskList.removeAt(mBoard.taskList.size-1)
        val cardAssignedToArray = ArrayList<String>()
        cardAssignedToArray.add(FirestoreClass().getUserId())
        val card = Card(cardName,FirestoreClass().getUserId(),cardAssignedToArray)
        val cardList = mBoard.taskList[taskPosition].cardList
        cardList.add(card)
        val task = Task(mBoard.taskList[taskPosition].title,mBoard.taskList[taskPosition].createdBy,cardList)
        mBoard.taskList[taskPosition] = task
        FirestoreClass().addUpdateBoardById(this,mBoard,Constants.EDIT_TASK_CODE,oldBoard,taskPosition)
    }

    fun setMemberDetails(list: ArrayList<User>) {
        mBoardMemberDetails = list
        setupActionBar(mBoard.name)
        mBoard.taskList.add(Task("Title"))
        val adapter = TaskListItemsAdapter(this,mBoard.taskList)
        binding?.rvTaskList?.adapter = adapter
        binding?.rvTaskList?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding?.rvTaskList?.setHasFixedSize(true)
    }

    fun updateCardsInTaskList(position: Int, cardList: java.util.ArrayList<Card>) {
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        mBoard.taskList[position].cardList = cardList
        FirestoreClass().addUpdateBoardById(this@TaskListActivity, mBoard,Constants.EDIT_TASK_CODE)
    }

}