package com.deucate.earntobank

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class Util(val context: Context? = null) {


    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context).setTitle(title).setMessage(message)
            .setPositiveButton("OK") { _, _ -> }.show()
    }

    fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(smsTimeInMilis: Long): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis

        val now = Calendar.getInstance()

        return when {
            now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) -> "Today"

            now.get(Calendar.DATE) + smsTime.get(Calendar.DATE) == 1 -> "Tomorrow"

            else -> SimpleDateFormat("dd/MM/yyyy").format(Date(smsTimeInMilis))
        }
    }

    fun changeDataTVStatus(isDataAvailable: Boolean, textView: TextView) {
        if (isDataAvailable) {
            textView.visibility = View.INVISIBLE
        } else {
            textView.visibility = View.VISIBLE
        }
    }

}