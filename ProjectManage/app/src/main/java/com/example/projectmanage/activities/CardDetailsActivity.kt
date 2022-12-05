package com.example.projectmanage.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.projectmanage.R
import com.example.projectmanage.adapters.SelectedCardListItemsAdapter
import com.example.projectmanage.databinding.ActivityCardDetailsBinding
import com.example.projectmanage.dialogs.LabelColorListDialog
import com.example.projectmanage.dialogs.MembersListDialog
import com.example.projectmanage.firebase.FirestoreClass
import com.example.projectmanage.models.Board
import com.example.projectmanage.models.Card
import com.example.projectmanage.models.SelectedMembers
import com.example.projectmanage.models.User
import com.example.projectmanage.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    private var binding: ActivityCardDetailsBinding? = null

    private lateinit var mBoard: Board
    private lateinit var mBoardMemberDetails: ArrayList<User>
    private var taskPosition = -1
    private var cardPosition = -1
    private var mSelectedColor: String = ""
    private var mSelectedDueDateMilliSeconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getDataFromIntent()
        setupActionBar(mBoard.taskList[taskPosition].cardList[cardPosition].title)

        binding?.etNameCardDetails?.setText(mBoard.taskList[taskPosition].cardList[cardPosition].title)

        mSelectedColor = mBoard.taskList[taskPosition].cardList[cardPosition].color
        if (!mSelectedColor.isNullOrEmpty()) {
            setLabelColor()
        }

        mSelectedDueDateMilliSeconds = mBoard.taskList[taskPosition].cardList[cardPosition].dueDate
        if (mSelectedDueDateMilliSeconds > 0) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDateFormat.format(Date(mSelectedDueDateMilliSeconds))
            binding?.tvSelectDueDate?.text = selectedDate
        }

        setupSelectedMembersGrid()

        binding?.tvSelectMembers?.setOnClickListener {
            openMemberSelectionDialog()
        }

        binding?.btnUpdateCardDetails?.setOnClickListener {
            if (binding?.etNameCardDetails?.text?.isEmpty() == true) {
                Toast.makeText(this, "Please enter card name", Toast.LENGTH_LONG).show()
            } else {
                updateCardDetails()
            }
        }

        binding?.tvSelectLabelColor?.setOnClickListener {
            openColorSelectionDialog()
        }

        binding?.tvSelectDueDate?.setOnClickListener {
            showDataPicker()
        }
    }

    private fun showDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year =
            c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                /*
                  The listener used to indicate the user has finished selecting a date.
                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                 Here the selected date is set into format i.e : day/Month/Year
                  And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                val sMonthOfYear =
                    if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                // Selected date it set to the TextView to make it visible to user.
                binding?.tvSelectDueDate?.text = selectedDate

                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                /**
                 * Here we have taken an instance of Date Formatter as it will format our
                 * selected date in the format which we pass it as an parameter and Locale.
                 * Here I have passed the format as dd/MM/yyyy.
                 */
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                // The formatter will parse the selected date in to Date object
                // so we can simply get date in to milliseconds.
                val theDate = sdf.parse(selectedDate)

                /** Here we have get the time in milliSeconds from Date object
                 */

                /** Here we have get the time in milliSeconds from Date object
                 */
                mSelectedDueDateMilliSeconds = theDate!!.time
            },
            year,
            month,
            day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }

    private fun openMemberSelectionDialog() {

        val cardMembers = mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo

        //checking if any board member is assigned to this card
        if (cardMembers.size > 0) {
            for (i in mBoardMemberDetails.indices) {
                for (j in cardMembers) {
                    if (j == mBoardMemberDetails[i].id) {
                        mBoardMemberDetails[i].selected = 1
                        continue
                    }
                }
            }
        } else {
            //if no board member assigned to this card, make their selected property false
            for (user in mBoardMemberDetails) user.selected = 0
        }


        val dialog =
            object : MembersListDialog(this, "Select members to assign", mBoardMemberDetails) {
                override fun onItemSelected(user: User, action: String) {
                    if (action == Constants.SELECT) {
                        if (!mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo.contains(
                                user.id
                            )
                        ) {
                            mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo.add(user.id)
                            for (i in mBoardMemberDetails) {
                                if (i.id == user.id) {
                                    i.selected = 1
                                    break
                                }
                            }
                        }
                    } else {
                        mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo.remove(user.id)
                        for (i in mBoardMemberDetails) {
                            if (i.id == user.id) {
                                i.selected = 0
                                break
                            }
                        }
                    }

                    setupSelectedMembersGrid()
                }
            }

        dialog.show()
    }

    private fun setupSelectedMembersGrid() {
        val cardMembers = mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo
        val selectedMembersList = ArrayList<SelectedMembers>()

        for (i in mBoardMemberDetails.indices) {
            for (j in cardMembers) {
                if (j == mBoardMemberDetails[i].id) {
                    selectedMembersList.add(
                        SelectedMembers(
                            mBoardMemberDetails[i].id,
                            mBoardMemberDetails[i].image
                        )
                    )

                }
            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding?.tvSelectMembers?.visibility = View.GONE
            binding?.rvSelectedMembersList?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.layoutManager = GridLayoutManager(this, 6)
            val adapter = SelectedCardListItemsAdapter(this, selectedMembersList,true)
            binding?.rvSelectedMembersList?.adapter = adapter
            adapter.clickListener = object : SelectedCardListItemsAdapter.OnClickListener {
                override fun onClick() {
                    openMemberSelectionDialog()
                }
            }

        } else {
            binding?.tvSelectMembers?.visibility = View.VISIBLE
            binding?.rvSelectedMembersList?.visibility = View.GONE
        }
    }

    private fun openColorSelectionDialog() {
        val colorList = Constants.getColorsList()
        val dialog = object : LabelColorListDialog(this, colorList, "Select label color") {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setLabelColor()
            }
        }

        dialog.show()
    }

    private fun setLabelColor() {
        binding?.tvSelectLabelColor?.text = ""
        binding?.tvSelectLabelColor?.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                deleteCard()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDataFromIntent() {
        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            mBoard = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }

        if (intent.hasExtra(Constants.TASK_LIST_POSITION)) {
            taskPosition = intent.getIntExtra(Constants.TASK_LIST_POSITION, -1)
        }

        if (intent.hasExtra(Constants.CARD_LIST_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_LIST_POSITION, -1)
        }

        if (intent.hasExtra(Constants.BOARD_MEMBERS_DETAILS)) {
            mBoardMemberDetails =
                intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_DETAILS)!!
        }
    }

    private fun setupActionBar(title: String) {
        setSupportActionBar(binding?.toolbarCardDetailsActivity)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
        binding?.toolbarCardDetailsActivity?.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun updateCardDetails() {
        val card = Card(
            binding?.etNameCardDetails?.text?.toString()!!,
            mBoard.taskList[taskPosition].cardList[cardPosition].createdBy,
            mBoard.taskList[taskPosition].cardList[cardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDateMilliSeconds
        )

        val tasks = mBoard.taskList
        tasks.removeAt(tasks.size-1)
        mBoard.taskList[taskPosition].cardList[cardPosition] = card
        FirestoreClass().addUpdateBoardById(this, mBoard, Constants.EDIT_TASK_CODE)
    }

    private fun deleteCard() {
        val taskList = mBoard.taskList
        taskList.removeAt(taskList.size - 1)
        val cardList = mBoard.taskList[taskPosition].cardList
        cardList.removeAt(cardPosition)

        FirestoreClass().addUpdateBoardById(this, mBoard, Constants.EDIT_TASK_CODE)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun editedTaskSuccessAction() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}