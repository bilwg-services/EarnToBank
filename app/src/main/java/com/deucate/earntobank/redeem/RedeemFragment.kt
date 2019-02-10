package com.deucate.earntobank.redeem


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import kotlinx.android.synthetic.main.fragment_redeem.view.*

class RedeemFragment : Fragment() {

    private val pointsPerRupee = HomeActivity.pointsPerRupee
    private val totalPoints = HomeActivity.totalPoints

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_redeem, container, false)

        val totalEarningInRupee = totalPoints / pointsPerRupee

        rootView.redeemTotalAmount.text = "₹ $totalEarningInRupee"

        rootView.redeemTotalSendBtn.setOnClickListener {
            if (totalEarningInRupee <= 100) {
                Toast.makeText(activity, "Reach ₹100 to withdraw money.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        return rootView
    }


}
