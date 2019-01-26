package com.deucate.earntobank

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


class Util(val context: Context? = null) {


    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("OK") { _, _ -> }.show()
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

    fun getTypeOfTeam(team: Int): String {
        return when (team) {
            1 -> "Solo"
            2 -> "Duo"
            4 -> "Squad"
            else -> "Undefined"
        }
    }

    interface OnCallBack {
        fun onAlertDialogPositiveAction(id: Int, dialog: DialogInterface?, which: Int)
        fun onAlertDialogNegativeAction(id: Int, dialog: DialogInterface?, which: Int)
    }

}