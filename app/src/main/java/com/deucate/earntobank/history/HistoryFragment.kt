package com.deucate.earntobank.history


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earntobank.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_history.view.*

class HistoryFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance().collection(getString(R.string.app_name)).document("App")

    private val histories = ArrayList<History>()
    private val adapter = HistoryAdapter(histories)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_history, container, false)

        val recyclerView = rootView.historyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        db.collection(getString(R.string.withdrawel)).whereEqualTo("UID",auth.uid!!).get().addOnCompleteListener {
            if (it.isSuccessful){
                val result = it.result!!
                if (!result.isEmpty){
                    for (data in result){
                        histories.add(
                            History(
                                id = data.id,
                                amount = data.getLong("Amount")!!,
                                mobileNumber = data.getString("MobileNumber")!!,
                                status = data.getBoolean("Status")!!
                            )
                        )
                    }
                    rootView.historyDataNotFound.visibility = View.INVISIBLE
                    adapter.notifyDataSetChanged()
                }
            }else{
                AlertDialog.Builder(activity).setTitle("Error").setMessage(it.exception!!.localizedMessage).show()
            }
        }


        return rootView
    }

}
