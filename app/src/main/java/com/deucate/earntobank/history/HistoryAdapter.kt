package com.deucate.earntobank.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earntobank.R
import kotlinx.android.synthetic.main.card_history.view.*

class HistoryAdapter(private val histories: ArrayList<History>) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_history,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = histories[position]

        holder.amount.text = "₹ ${history.amount}"
        holder.mobileNumber.text = history.mobileNumber
        holder.status.text = if (history.status) {
            "Done"
        } else {
            "Pending"
        }
    }

}

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val mobileNumber = view.historyMobileNumber!!
    val status = view.historyStatus!!
    val amount = view.historyAmount!!
}