package com.deucate.earntobank.alert

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deucate.earntobank.R
import kotlinx.android.synthetic.main.card_alert.view.*

class AlertAdapter(private val alerts: ArrayList<Alert>, private val listner: OnClickListener) :
    RecyclerView.Adapter<AlertViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        return AlertViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_alert,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]

        holder.title.text = alert.title
        holder.time.text = alert.time
        holder.message.text = alert.message

        holder.cardView.setOnClickListener {
            listner.onClick(position)
        }
    }

}

class AlertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardView = view.cardAlertCardView!!
    val title = view.cardAlertTitle!!
    val time = view.cardAlertTime!!
    val message = view.cardAlertMessage!!
}