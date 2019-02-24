package com.deucate.earntobank.home


import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.deucate.earntobank.MainAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var util: Util
    private val db = FirebaseFirestore.getInstance().collection(getString(R.string.app_name)).document("App")
    private val homeData = ArrayList<Home>()
    private lateinit var adapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        util = Util(activity as Context)
        getData()

        adapter = MainAdapter(
            homeData,
            object : MainAdapter.OnClickHomeCard {
                override fun onClickLink(link: String) {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        startActivity(browserIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(activity,"This is not url!! is it?",Toast.LENGTH_SHORT).show()
                    }
                }
            })


        val recyclerView = rootView.homeRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        return rootView
    }

    private fun getData() {
        db.collection("Home").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result!!) {
                    homeData.add(
                        Home(
                            data.getString("Title")!!,
                            data.getString("PostDate")!!,
                            data.getString("Link")!!
                        )
                    )
                }
                adapter.notifyDataSetChanged()
                if (!(it.result!!.isEmpty)) {
                    homeNotFoundTV.visibility = View.INVISIBLE
                }
            } else {
                util.showToastMessage(it.exception!!.localizedMessage)
            }
        }

    }

    data class Home(
        val Title: String,
        val PostDate: String,
        val Link: String
    )

}
