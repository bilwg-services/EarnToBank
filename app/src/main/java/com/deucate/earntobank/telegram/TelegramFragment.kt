package com.deucate.earntobank.telegram


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.deucate.earntobank.MainAdapter
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.deucate.earntobank.home.HomeFragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_telegram.*
import kotlinx.android.synthetic.main.fragment_telegram.view.*


class TelegramFragment : Fragment() {

    private lateinit var util: Util
    private val db = FirebaseFirestore.getInstance().collection(getString(R.string.app_name)).document("App")
    private val telegramData = ArrayList<HomeFragment.Home>()
    private lateinit var adaper: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_telegram, container, false)

        util = Util(activity as Context)
        getData()

        adaper = MainAdapter(
            telegramData,
            object : MainAdapter.OnClickHomeCard {
                override fun onClickLink(link: String) {
                    try {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        startActivity(browserIntent)
                    } catch (e: ActivityNotFoundException) {
                        util.showToastMessage("Link not valid.")
                    }
                }
            })


        val recyclerView = rootView.telegramRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adaper

        return rootView
    }

    private fun getData() {
        db.collection("Telegram").get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (data in it.result!!) {
                    telegramData.add(
                        HomeFragment.Home(
                            data.getString("Title")!!,
                            data.getString("Time")!!,
                            data.getString("Link")!!
                        )
                    )
                }
                adaper.notifyDataSetChanged()
                if (!(it.result!!.isEmpty)) {
                    telegramTextView.visibility = View.INVISIBLE
                }
            } else {
                util.showToastMessage(it.exception!!.localizedMessage)
            }
        }

    }

}
