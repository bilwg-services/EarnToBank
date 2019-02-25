package com.deucate.earntobank

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast


class Util(val context: Context? = null) {

    companion object {
        const val tableName = "EarnTable"
        const val id = "ID"
        const val title = "Title"
        const val message = "Message"
        const val time = "Time"

        const val query =
            "CREATE TABLE $tableName($id INTEGER PRIMARY KEY, $title TEXT, $message TEXT, $time Text); "
    }


    fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context).setTitle(title).setMessage(message)
            .setPositiveButton("OK") { _, _ -> }.show()
    }

    fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}