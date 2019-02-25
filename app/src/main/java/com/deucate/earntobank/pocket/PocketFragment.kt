package com.deucate.earntobank.pocket


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_pocket.view.*


class PocketFragment : Fragment() {

    private lateinit var db: DocumentReference
    private val auth = FirebaseAuth.getInstance()

    private val totalPoint = HomeActivity.totalPoints
    private val pointsPerRupee = HomeActivity.pointsPerRupee

    private var totalEarn = MutableLiveData<Long>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_pocket, container, false)
        db = FirebaseFirestore.getInstance().collection("Apps")
            .document(getString(R.string.app_name))
        val totalAmount = totalPoint / pointsPerRupee
        rootView.pocketTotalAmount.text = totalAmount.toString()
        totalEarn.value = totalAmount

        getReferralPoints(rootView)
        getRechargedAmount(rootView)

        totalEarn.observe(this, Observer {
            if (it != null) {
                rootView.pocketTotalEarn.text = "₹ $it"
            }
        })

        return rootView
    }

    @SuppressLint("SetTextI18n")
    private fun getReferralPoints(rootView: View) {
        db.collection(getString(R.string.users)).document(auth!!.uid!!)
            .collection(getString(R.string.references)).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val referralEarn = it.result!!.size() * 50L
                    totalEarn.value = totalEarn.value?.plus(referralEarn)
                    rootView.pocketReferralEarn.text = "₹ $referralEarn"
                } else {
                    AlertDialog.Builder(activity).setTitle("Error")
                        .setMessage(it.exception!!.localizedMessage).show()
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun getRechargedAmount(rootView: View) {
        db.collection(getString(R.string.withdrawel)).whereEqualTo("UID", auth!!.uid!!).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var recharged = 0L
                    for (doc in it.result!!) {
                        recharged += doc.getLong("Amount")!!
                    }
                    totalEarn.value = totalEarn.value?.plus(recharged)
                    rootView.pocketRecharge.text = "₹ $recharged"
                } else {
                    AlertDialog.Builder(activity).setTitle("Error")
                        .setMessage(it.exception!!.localizedMessage).show()
                }
            }
    }


}
