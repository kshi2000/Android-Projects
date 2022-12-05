package com.example.projectmanage.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanage.activities.TaskListActivity
import com.example.projectmanage.databinding.ItemTaskBinding
import com.example.projectmanage.models.Task
import java.util.*
import kotlin.collections.ArrayList

class TaskListItemsAdapter(val context: Context, private var datalist:ArrayList<Task>):
    RecyclerView.Adapter<TaskListItemsAdapter.ViewHolder>() {

    private var mPositionDraggedFrom = -1
    // A global variable for position dragged TO.
    private var mPositionDraggedTo = -1

    class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int):ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(15,0,40,0)
        binding.root.layoutParams = layoutParams
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    private fun Int.toDP():Int = (this/ (Resources.getSystem().displayMetrics.density).toInt())
    private fun Int.toPX():Int = (this * (Resources.getSystem().displayMetrics.density).toInt())

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val model = datalist[position]

        if (position == datalist.size - 1) {
            holder.binding.llTaskItem.visibility = View.GONE
            holder.binding.tvAddTaskList.visibility = View.VISIBLE
        } else {
            holder.binding.llTaskItem.visibility = View.VISIBLE
            holder.binding.tvAddTaskList.visibility = View.GONE
        }

        holder.binding.tvTaskListTitle.text = datalist[position].title

        holder.binding.tvAddTaskList.setOnClickListener {
            holder.binding.cvAddTaskListName.visibility = View.VISIBLE
            holder.binding.tvAddTaskList.visibility = View.GONE
        }

        holder.binding.ibCloseListName.setOnClickListener {
            holder.binding.cvAddTaskListName.visibility = View.GONE
            holder.binding.tvAddTaskList.visibility = View.VISIBLE
        }

        holder.binding.ibDoneListName.setOnClickListener {

            if (context is TaskListActivity) {
                if (holder.binding.etTaskListName.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_LONG).show()
                } else {
                    val taskName = holder.binding.etTaskListName.text
                    context.createTask(taskName.toString())
                    holder.binding.tvAddTaskList.visibility = View.GONE
                }
            }
        }

        holder.binding.ibEditListName.setOnClickListener {
            holder.binding.etEditTaskListName.setText(model.title)
            holder.binding.llTitleView.visibility = View.GONE
            holder.binding.cvEditTaskListName.visibility = View.VISIBLE
        }

        holder.binding.ibCloseEditableView.setOnClickListener {
            holder.binding.llTitleView.visibility = View.VISIBLE
            holder.binding.cvEditTaskListName.visibility = View.GONE
        }

        holder.binding.ibDoneEditListName.setOnClickListener {
            if (context is TaskListActivity) {
                if (holder.binding.etEditTaskListName.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_LONG).show()
                } else {
                    val taskName = holder.binding.etEditTaskListName.text
                    context.editTask(taskName.toString(), position, model)
                }
            }
        }

        holder.binding.ibDeleteList.setOnClickListener {
            if (context is TaskListActivity) {
                context.deleteTask(position)
            }
        }

        holder.binding.tvAddCard.setOnClickListener {
            holder.binding.cvAddCard.visibility = View.VISIBLE
            holder.binding.tvAddCard.visibility = View.GONE
        }

        holder.binding.ibCloseCardName.setOnClickListener {
            holder.binding.cvAddCard.visibility = View.GONE
            holder.binding.tvAddCard.visibility = View.VISIBLE
        }

        holder.binding.ibDoneCardName.setOnClickListener {
            if (context is TaskListActivity) {
                if (holder.binding.etCardName.text.isNullOrEmpty()) {
                    Toast.makeText(context, "Please enter something", Toast.LENGTH_LONG).show()
                } else {
                    val cardName = holder.binding.etCardName.text.toString()
                    context.createCard(position, cardName)
                }
            }
        }

        holder.binding.rvCardList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        holder.binding.rvCardList.setHasFixedSize(true)

        val adapter = CardListAdapter(context, datalist[position].cardList)
        adapter.setOnClickListener(object : CardListAdapter.OnClickListener {
            override fun onClick(cardPosition: Int) {
                if (context is TaskListActivity) {
                    context.moveToCardDetailsActivity(position, cardPosition)
                }
            }

        })
        holder.binding.rvCardList.adapter = adapter

        /**
         * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
         * {@link LinearLayoutManager}.
         *
         * @param context Current context, it will be used to access resources.
         * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
         */
        val dividerItemDecoration =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        holder.binding.rvCardList.addItemDecoration(dividerItemDecoration)

        //  Creates an ItemTouchHelper that will work with the given Callback.
        val helper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            /*Called when ItemTouchHelper wants to move the dragged item from its old position to
             the new position.*/
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val draggedPosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition

                // TODO (Step 4: Assign the global variable with updated values.)
                // START
                if (mPositionDraggedFrom == -1) {
                    mPositionDraggedFrom = draggedPosition
                }
                mPositionDraggedTo = targetPosition
                // END

                /**
                 * Swaps the elements at the specified positions in the specified list.
                 */

                /**
                 * Swaps the elements at the specified positions in the specified list.
                 */
                Collections.swap(datalist[position].cardList, draggedPosition, targetPosition)

                // move item in `draggedPosition` to `targetPosition` in adapter.
                adapter.notifyItemMoved(draggedPosition, targetPosition)

                return false // true if moved, false otherwise
            }

            // Called when a ViewHolder is swiped by the user.
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { // remove from adapter
            }

            // TODO (Step 5: Finally when the dragging is completed than call the function to update the cards in the database and reset the global variables.)
            // START
            /*Called by the ItemTouchHelper when the user interaction with an element is over and it
             also completed its animation.*/
            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                    (context as TaskListActivity).updateCardsInTaskList(
                        position,
                        datalist[position].cardList
                    )
                }

                // Reset the global variables
                mPositionDraggedFrom = -1
                mPositionDraggedTo = -1
            }
        })

        helper.attachToRecyclerView(holder.binding.rvCardList)
    }
    }