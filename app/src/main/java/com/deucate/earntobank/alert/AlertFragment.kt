package com.deucate.earntobank.alert


import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earntobank.DatabseHalper
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import kotlinx.android.synthetic.main.fragment_alert.view.*
import timber.log.Timber

class AlertFragment : Fragment() {

    private val alerts = ArrayList<Alert>()
    private lateinit var database: SQLiteDatabase

    private lateinit var adapter: AlertAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_alert, container, false)

        adapter = AlertAdapter(alerts, object : AlertAdapter.OnClickListener {
            override fun onClick(position: Int) {}

        })

        val recyclerView = rootView.alertRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        val dbHelper = DatabseHalper(activity, Util.tableName, null, 1)
        database = dbHelper.readableDatabase
        val cursor = database.rawQuery("SELECT * FROM ${Util.tableName};", null)
        getDataFromDatabase(cursor)

        return rootView
    }

    private fun getDataFromDatabase(cursor: Cursor) {
        while (cursor.moveToNext()) {
            alerts.add(
                Alert(
                    id = cursor.getString(cursor.getColumnIndex(Util.id)),
                    title = cursor.getString(cursor.getColumnIndex(Util.title)),
                    message = cursor.getString(cursor.getColumnIndex(Util.message)),
                    time = cursor.getString(cursor.getColumnIndex(Util.time))
                )
            )
        }
        adapter.notifyDataSetChanged()
    }
}
