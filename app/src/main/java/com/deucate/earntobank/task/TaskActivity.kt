package com.deucate.earntobank.task

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_task.*
import timber.log.Timber

class TaskActivity : AppCompatActivity() {

    private lateinit var interstitialAdID: String
    private var currentTaskImpression = MutableLiveData<Long>()
    private var currentTaskClick = MutableLiveData<Long>()
    private var status = false

    private val auth = FirebaseAuth.getInstance()
    private lateinit var db: DocumentReference

    private lateinit var util: Util

    private lateinit var mInterstitialAd: InterstitialAd

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        interstitialAdID = intent.getStringExtra("InterstitialID")
        currentTaskImpression.value = intent.getLongExtra("CurrentTaskImpression", 0)
        currentTaskClick.value = intent.getLongExtra("CurrentTaskClick", 0)
        status = intent.getBooleanExtra("Status", false)

        if (status){
            taskClickCounter.text = "10/10"
            taskImpressionCount.text = "10/10"
            return
        }
        db = FirebaseFirestore.getInstance().collection("Apps")
            .document(getString(R.string.app_name))
        util = Util(this)

        MobileAds.initialize(this, getString(R.string.app_id))

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = interstitialAdID
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: Int) {
                when (p0) {
                    AdRequest.ERROR_CODE_INTERNAL_ERROR -> {
                        Toast.makeText(
                            this@TaskActivity,
                            "Something happened internally; for instance, an invalid response was received from the ad server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    AdRequest.ERROR_CODE_INVALID_REQUEST -> {
                        Toast.makeText(
                            this@TaskActivity,
                            " The ad request was invalid; for instance, the ad unit ID was incorrect.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    AdRequest.ERROR_CODE_NETWORK_ERROR -> {
                        Toast.makeText(
                            this@TaskActivity,
                            "The ad request was unsuccessful due to network connectivity.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    AdRequest.ERROR_CODE_NO_FILL -> {
                        Toast.makeText(
                            this@TaskActivity,
                            "The ad request was successful, but no ad was returned due to lack of ad inventory.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                super.onAdFailedToLoad(p0)
            }

            override fun onAdLoaded() {
                Timber.d("Ad loded")
                super.onAdLoaded()
            }
        }


        taskStartButton.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Timber.d("The interstitial wasn't loaded yet.")
            }
        }

        currentTaskClick.observe(this, Observer {
            val data = HashMap<String, Any>()
            data["CurrentTaskClick"] = it!!

            db.collection(getString(R.string.users)).document(auth.uid!!).update(data)
                .addOnCompleteListener { dbResult ->
                    if (dbResult.isSuccessful) {
                        taskClickCounter.text = "${currentTaskClick.value}/10"
                    } else {
                        util.showToastMessage("Error : ${dbResult.exception!!.localizedMessage}")
                    }
                }
        })

        currentTaskImpression.observe(this, Observer {
            val data = HashMap<String, Any>()
            data["CurrentTaskImpression"] = it!!

            db.collection(getString(R.string.users)).document(auth.uid!!).update(data)
                .addOnCompleteListener { dbResult ->
                    if (dbResult.isSuccessful) {
                        taskImpressionCount.text = "${currentTaskImpression.value}/10"
                    } else {
                        util.showToastMessage("Error : ${dbResult.exception!!.localizedMessage}")
                    }
                }
        })

    }

}
