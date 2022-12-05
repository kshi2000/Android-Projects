package com.example.projectmanage.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.projectmanage.activities.*
import com.example.projectmanage.models.Board
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject


class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    //registers user in the firestore and finishes activity
    fun registerUser(activity: SignupActivity, userInfo: User) {
        mFirestore.collection(Constants.USERS).document(getUserId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.userRegisterSuccess()
            activity.finish()
        }.addOnFailureListener { exception ->
            Log.d("Register User Fail", "get failed with ", exception)
        }
    }

    fun createBoard(activity:CreateBoardActivity,boardInfo:Board){
        mFirestore.collection(Constants.BOARDS).document()
            .set(boardInfo, SetOptions.merge()).addOnSuccessListener {
                Toast.makeText(activity,"Created board",Toast.LENGTH_SHORT).show()
                activity.createdBoardSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressBar()
                Toast.makeText(activity,exception.message,Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUser(activity: Activity, userInfo: HashMap<String, Any>) {
        if(activity is MainActivity) activity.showProgressBar()
        mFirestore.collection(Constants.USERS).document(getUserId()).update(userInfo)
            .addOnSuccessListener {
                Toast.makeText(activity, "Successfully updated details", Toast.LENGTH_LONG).show()
                if(activity is ProfileActivity)  activity.showUpdateSuccess()
                if(activity is MainActivity) activity.hideProgressBar()
                if(activity is MainActivity) activity.tokenUpdatedSuccess()
            }
            .addOnFailureListener { e ->
                if(activity is ProfileActivity) activity.hideProgressBar()
                if(activity is MainActivity) activity.hideProgressBar()
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG)
            }
    }

    //fetches user details after logging in
    fun getUserDocument(activity: Activity) {
        val docRef = mFirestore.collection(Constants.USERS).document(getUserId())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val info = document.toObject(User::class.java)!!
                    when (activity) {
                        is SigninActivity -> activity.signinSuccess(info)
                        is MainActivity -> activity.updateUserNavDetails(info)
                        is ProfileActivity -> activity.loadProfile(info)
                    }

                } else {
                    Log.d("Doc not found: ", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FetchingUserDetailsFail", "get failed with ", exception)
            }
    }

    fun getBoardDocuments(activity: MainActivity){
        activity.showProgressBar()
        mFirestore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO,getUserId()).get().addOnFailureListener { it->
            activity.hideProgressBar()
            Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
        }
            .addOnSuccessListener{
                document->
                activity.hideProgressBar()
                val parsedList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    var board = i.toObject(Board::class.java)
                    board!!.documentId = i.id
                    parsedList.add(board)
                }
                activity.populateBoardsUi(parsedList)
            }
    }

    fun getBoardById(activity: TaskListActivity,id:String){
        activity.showProgressBar()
        mFirestore.collection(Constants.BOARDS).document(id).get().addOnFailureListener { it->
            Toast.makeText(activity,it.message,Toast.LENGTH_LONG).show()
        }
            .addOnSuccessListener{
                    document->
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.setRecyclerView(board)
            }
    }

    fun addUpdateBoardById(activity: Activity,board: Board,operationCode:Int,oldBoard: Board? = null,positionToBeChangedOrDeleted:Int? = null){
        val tasksHashmap:HashMap<String,Any> = HashMap()
        tasksHashmap[Constants.TASK_LIST] = board.taskList

        if (activity is TaskListActivity) activity.showProgressBar()
        if (activity is CardDetailsActivity) activity.showProgressBar()

        mFirestore.collection(Constants.BOARDS).document(board.documentId).update(tasksHashmap).addOnSuccessListener{
            if(activity is TaskListActivity) activity.hideProgressBar()
            if(activity is CardDetailsActivity) activity.hideProgressBar()

            if(operationCode == Constants.ADD_TASK_CODE)
            {
                if(activity is TaskListActivity) activity.addedTaskSuccessNotification()
            }
            else if(operationCode == Constants.EDIT_TASK_CODE){
                if(activity is TaskListActivity) activity.editedTaskSuccessNotification(positionToBeChangedOrDeleted)
                if(activity is CardDetailsActivity) activity.editedTaskSuccessAction()
            }
            else if(operationCode == Constants.DELETE_TASK_CODE){
                if(activity is TaskListActivity) activity.deletedTaskSuccessNotification(positionToBeChangedOrDeleted)
            }
        }
            .addOnFailureListener { e->
                if(activity is TaskListActivity) activity.hideProgressBar()
                if(activity is CardDetailsActivity) activity.hideProgressBar()
                Toast.makeText(activity,e.message,Toast.LENGTH_LONG).show()
            }
    }

    fun getBoardMembersDetails(activity: Activity,userIds:ArrayList<String>){

        if(activity is MembersActivity)  activity.showProgressBar()
        mFirestore.collection(Constants.USERS).whereIn(Constants.ID,userIds).get().addOnSuccessListener {
            document->
            val list = ArrayList<User>()
            for(user in document.documents){
                val i = user.toObject(User::class.java)
                list.add(i!!)
            }
            if(activity is MembersActivity) activity.setupRecyclerView(list)
            if(activity is MembersActivity) activity.hideProgressBar()

            if(activity is TaskListActivity) activity.setMemberDetails(list)
            if(activity is TaskListActivity) activity.hideProgressBar()
        }.addOnFailureListener {
            e->
            Toast.makeText(activity,e.message,Toast.LENGTH_LONG).show()
            if(activity is MembersActivity) activity.hideProgressBar()
            if(activity is TaskListActivity) activity.hideProgressBar()
        }
    }

    fun addUserToBoard(activity: MembersActivity,board: Board,user: User){
        val assignedToHashmap = HashMap<String,Any>()
        assignedToHashmap[Constants.ASSIGNED_TO] = board.assignedTo
        mFirestore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashmap).addOnSuccessListener {
            activity.addUserToBoardSuccess(user)
            activity.hideProgressBar()
        }.addOnFailureListener {
            Toast.makeText(activity,"Something went wrong",Toast.LENGTH_LONG).show()
            activity.addUserToBoardFailure()
            activity.hideProgressBar()
        }
    }

    fun getUserByEmail(activity: MembersActivity,email:String){
        activity.showProgressBar()
        mFirestore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL,email).get().addOnSuccessListener {
            document->
            if(document.documents.size>0){
                val user = document.documents[0].toObject(User::class.java)
                activity.assignUserToBoard(user)
            }
            else{
                activity.hideProgressBar()
                activity.showErrorSnackBar("No such user exists in the app")
            }
        }.addOnFailureListener {
            e->
            activity.hideProgressBar()
            Toast.makeText(activity,e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun getUserId(): String {
        var id = ""
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            id = user.uid
        }
        return id
    }
}