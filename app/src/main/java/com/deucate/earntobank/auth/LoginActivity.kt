package com.deucate.earntobank.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.deucate.earntobank.group.RefUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val signIn = 68
    private val util = Util(this)

    private lateinit var db: DocumentReference

    @Suppress("UnresolvedReference")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            startHomeActivity()
        }
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance().collection("Apps")
            .document(getString(R.string.app_name))

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                3
            )
        }


        googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        findViewById<SignInButton>(R.id.loginGSB).setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, signIn)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            signIn -> {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)
                    signInToFirebase(account!!)
                } catch (e: ApiException) {
                    util.showAlertDialog("Error", e.localizedMessage)
                } catch (e: NullPointerException) {
                    util.showAlertDialog("Error", e.localizedMessage)
                }
            }
            69 -> {
                if (resultCode == Activity.RESULT_OK) {
                    val user = data!!.getSerializableExtra("user") as User
                    val refUser = RefUser(
                        Name = user.Name,
                        Time = Timestamp.now(),
                        ImageURL = user.ImageURL,
                        uid = user.UID
                    )
                    addReferUser(refUser)
                } else {
                    util.showToastMessage("No user found.")
                    startHomeActivity()
                }
            }
        }
    }

    private fun checkNewUser() {
        db.collection(getString(R.string.users)).document(auth.uid!!).get().addOnCompleteListener {
            val result = it.result!!
            if (it.isSuccessful) {
                val name = result.getString("Name")
                if (name.isNullOrEmpty()) {
                    //new user
                    addReferUser()
                } else {
                    //old user
                    startHomeActivity()
                }
            }
        }
    }

    private fun registerNewUser() {
        val user = User(
            Name = auth.currentUser!!.displayName!!,
            Email = auth.currentUser!!.email!!,
            ImageURL = auth.currentUser!!.photoUrl.toString(),
            UID = auth.uid!!
        )

        db.collection(getString(R.string.users)).document(auth.uid!!).set(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Welcome ${auth.currentUser!!.displayName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                startHomeActivity()
            }

    }

    private fun addReferUser() {
        AlertDialog.Builder(this).setTitle("Refer").setMessage("Do you have refer code?")
            .setPositiveButton("Yes") { _, _ ->
                startActivityForResult(Intent(this, ReferActivity::class.java), 69)
            }.setNegativeButton("No") { _, _ ->
                registerNewUser()
            }.show()
    }

    private fun addReferUser(ref: RefUser) {

        db.collection(getString(R.string.users)).document(ref.uid)
            .collection(getString(R.string.ref)).add(ref).addOnCompleteListener {
                registerNewUser()
            }
    }

    private fun signInToFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    checkNewUser()
                } else {
                    util.showAlertDialog("Error", task.exception!!.localizedMessage)
                }
            }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

}
