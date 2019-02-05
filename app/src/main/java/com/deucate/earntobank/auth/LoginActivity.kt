package com.deucate.earntobank.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deucate.earntobank.HomeActivity
import com.deucate.earntobank.R
import com.deucate.earntobank.Util
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val signIn = 69
    private val util = Util(this)

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            startHomeActivity()
        }
        setContentView(R.layout.activity_login)

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
        }
    }

    private fun checkNewUser() {
        db.collection(getString(R.string.users)).document(auth.uid!!).get().addOnCompleteListener {
            val result = it.result!!
            if (it.isSuccessful) {
                val name = result.getString("Name")
                if (name.isNullOrEmpty()) {
                    //new user
                    registerNewUser()
                } else {
                    //old user
                    startHomeActivity()
                }
            }
        }
    }

    private fun registerNewUser() {
        val data = HashMap<String, Any>()
        data["Name"] = auth.currentUser!!.displayName!!
        data["Email"] = auth.currentUser!!.email!!

        db.collection(getString(R.string.users)).add(data).addOnCompleteListener {
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
