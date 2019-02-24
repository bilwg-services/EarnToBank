package com.deucate.earntobank.auth

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.deucate.earntobank.Util
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.deucate.earntobank.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.alert_profile_ref.view.*


class ReferActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var util: Util

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refer)

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)
        util = Util(this)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                getUserDetail(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            runOnUiThread {
                util.showToastMessage(it.localizedMessage)
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun getUserDetail(uid: String) {
        FirebaseFirestore.getInstance().collection(getString(R.string.app_name)).document("App").collection(getString(com.deucate.earntobank.R.string.users))
            .document(uid).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val result = it.result!!
                        val user = User(
                            Name = result.getString("Name")!!,
                            Email = result.getString("Email")!!,
                            ImageURL = result.getString("ImageURL")!!,
                            UID = result.id
                        )
                        showUserDetail(user)
                    } catch (e: NullPointerException) {
                        onBackPressed()
                    }

                } else {
                    util.showToastMessage(it.exception!!.localizedMessage!!)
                }
            }
    }

    @SuppressLint("InflateParams")
    private fun showUserDetail(user: User) {
        val view = LayoutInflater.from(this).inflate(R.layout.alert_profile_ref, null, false)

        Picasso.get().load(user.ImageURL).into(view.alertRefImageView)
        view.alertRefName.text = user.Name
        view.alertRefUID.text = user.UID

        AlertDialog.Builder(this).setView(view).setPositiveButton("Next") { _, _ ->
            val intent = Intent()
            intent.putExtra("user", user)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }.setNegativeButton("Cancel") { _, _ ->
            onBackPressed()
        }.setCancelable(false).show()
    }

    override fun onBackPressed() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
