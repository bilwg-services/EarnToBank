package com.deucate.earntobank

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast


class Util(val context: Context? = null) {


    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context).setTitle(title).setMessage(message)
            .setPositiveButton("OK") { _, _ -> }.show()
    }

    fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}