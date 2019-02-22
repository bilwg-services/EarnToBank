package com.deucate.earntobank.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earntobank.R
import kotlinx.android.synthetic.main.card_ref_user.view.*
import java.text.SimpleDateFormat

class GroupAdapter(private val data: ArrayList<RefUser>) : RecyclerView.Adapter<GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            LayoutInflater.from(parent.context!!).inflate(
                R.layout.card_ref_user,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = data[position]
        val dateFormat = SimpleDateFormat("hh:mm aa dd:MM:yy")
        val time = dateFormat.format(group.Time.toDate())

        holder.nameTv.text = group.Name
        holder.timeTv.text = time

    }
}

class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameTv = view.refCardName!!
    val timeTv = view.refCardDate!!
}