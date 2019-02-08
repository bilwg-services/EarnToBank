package com.deucate.earntobank.task

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earntobank.R
import kotlinx.android.synthetic.main.card_task.view.*

class TaskAdapter(private val tasks: ArrayList<Task>, private val listner: OnClickTaskCard) :
    RecyclerView.Adapter<TaskViewHolder>() {

    private val colors =
        arrayOf("#E91E63", "#9C27B0", "#3F51B5", "#F44336", "#FF5722", "#795548")
    private var currentColor = 0

    interface OnClickTaskCard {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context!!).inflate(
                R.layout.card_task,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTv.text = task.title
        holder.status.text = getStatus(task.status)
        holder.cardView.setOnClickListener {
            listner.onClick(position)
        }
        holder.cardView.setCardBackgroundColor(Color.parseColor(colors[currentColor]))
        currentColor += 1
        if (currentColor >= colors.size) {
            currentColor = 0
        }
        if (task.status){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"))
        }

    }

    private fun getStatus(status: Boolean): String {
        return if (status) {
            "Done"
        } else {
            "Pending"
        }
    }

}

class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleTv = view.cardTaskNumber!!
    val status = view.cardTaskStatus!!
    val cardView = view.cardTaskCardView!!
}