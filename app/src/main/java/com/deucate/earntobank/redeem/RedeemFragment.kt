@file:Suppress("DEPRECATION")

package com.deucate.earntobank.redeem

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_redeem.view.*

class RedeemFragment : Fragment() {

    private val pointsPerRupee = HomeActivity.pointsPerRupee
    private val totalPoints = HomeActivity.totalPoints

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var processDialog: AlertDialog

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_redeem, container, false)

        processDialog = ProgressDialog.show(activity, "Loading", "Your request is in process")

        val totalEarningInRupee = totalPoints / pointsPerRupee

        rootView.redeemTotalAmount.text = "₹ $totalEarningInRupee"

        rootView.redeemTotalSendBtn.setOnClickListener {
            if (totalEarningInRupee <= 100) {
                Toast.makeText(activity, "Reach ₹100 to withdraw money.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val mobileNumber = rootView.redeemMobileNumber.text.toString()
                val amount = rootView.redeemAmountToRedeem.text.toString()

                if (mobileNumber.isEmpty()) {
                    rootView.redeemMobileNumber.error = "Please enter your mobile number"
                    return@setOnClickListener
                }

                if (amount.isEmpty()) {
                    rootView.redeemAmountToRedeem.error = "Please enter amount to redeem"
                    return@setOnClickListener
                }

                val amountInLong = amount.toLong()

                if (amountInLong < 100) {
                    rootView.redeemAmountToRedeem.error = "Minimum withdrawal limit is 100 RS."
                    return@setOnClickListener
                }

                sendWithdrawalRequest(mobileNumber, amountInLong)
            }
        }

        return rootView
    }

    private fun sendWithdrawalRequest(mobileNumber: String, amountInLong: Long) {
        processDialog.show()
        val data = HashMap<String, Any>()
        data["MobileNumber"] = mobileNumber
        data["Amount"] = amountInLong
        data["UID"] = auth.uid!!

        db.collection(getString(R.string.withdrawel)).document().set(data).addOnCompleteListener {
            if (it.isSuccessful) {
                updateUserData(amountInLong)
            } else {
                processDialog.dismiss()
                AlertDialog.Builder(activity).setTitle("Error")
                    .setMessage(it.exception!!.localizedMessage).show()
            }
        }
    }

    private fun updateUserData(amount: Long) {
        val data = HashMap<String, Any>()
        data["TotalPoints"] = totalPoints - amount

        db.collection(getString(R.string.users)).document(auth.uid!!).update(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    processDialog.show()
                } else {
                    processDialog.dismiss()
                    HomeActivity.totalPoints = totalPoints - amount
                    AlertDialog.Builder(activity).setTitle("Error")
                        .setMessage(it.exception!!.localizedMessage).show()
                }
            }

    }


}
