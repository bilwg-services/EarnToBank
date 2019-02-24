package com.deucate.earntobank.task


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_task.view.*

class TaskFragment : Fragment(), TaskAdapter.OnClickTaskCard {

    private val tasks = MutableLiveData<ArrayList<Task>>()

    private val db = FirebaseFirestore.getInstance().collection(getString(R.string.app_name)).document("App")
    private val auth = FirebaseAuth.getInstance()

    private var lastTask = 0L
    private var currentTaskImpression = 0L
    private var currentTaskClick = 0L

    private lateinit var adapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_task, container, false)
        tasks.value = loadRecyclerViewData()

        adapter = TaskAdapter(tasks.value!!, this)
        val recyclerView = rootView.taskRecyclerView
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = adapter

        db.collection(getString(R.string.users)).document(auth.uid!!).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result!!
                try {
                    lastTask = result.getLong("LastTask")!!
                } catch (e: java.lang.NullPointerException) {
                    e.printStackTrace()
                }
                try {
                    currentTaskImpression = result.getLong("CurrentTaskImpression")!!
                } catch (e: java.lang.NullPointerException) {
                    e.printStackTrace()
                }
                try {
                    currentTaskClick = result.getLong("CurrentTaskClick")!!
                } catch (e: java.lang.NullPointerException) {
                    e.printStackTrace()
                }

                if (lastTask != 0L) {
                    for (i in 0..lastTask) {
                        tasks.value!![i.toInt()].status = true
                    }
                }
                rootView.taskDataNotFound.visibility = View.INVISIBLE
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun loadRecyclerViewData(): ArrayList<Task> {
        val arrayList = ArrayList<Task>()
        for (i in 1..10) {
            arrayList.add(Task("Task $i", false))
        }
        return arrayList
    }

    override fun onClick(position: Int) {
        val intent = Intent(activity, TaskActivity::class.java)
        intent.putExtra("Status", tasks.value!![position].status)
        intent.putExtra("CurrentTaskImpression", currentTaskImpression)
        intent.putExtra("InterstitialID", HomeActivity.interstitialAdID)
        intent.putExtra("CurrentTaskClick", currentTaskClick)
        startActivity(intent)
    }

}
