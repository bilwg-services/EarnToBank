package com.deucate.earntobank.task

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_task.*
import timber.log.Timber

class TaskActivity : AppCompatActivity() {

    private lateinit var interstitialAdID: String
    private var currentTaskImpression = MutableLiveData<Long>()
    private var currentTaskClick = MutableLiveData<Long>()
    private var status = false

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

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

        util = Util(this)

        MobileAds.initialize(this, getString(R.string.app_id))

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = interstitialAdID
        mInterstitialAd.loadAd(AdRequest.Builder().build())


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
                        taskClickCounter.text = "$currentTaskClick/10"
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
                        taskClickCounter.text = "$currentTaskImpression/10"
                    } else {
                        util.showToastMessage("Error : ${dbResult.exception!!.localizedMessage}")
                    }
                }
        })

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                currentTaskImpression.value!!.plus(1)
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                util.showToastMessage("Error while load ad. ERROR_CODE : $errorCode")
            }

            override fun onAdOpened() {
                currentTaskClick.value!!.plus(1)
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {}
        }

    }

}
