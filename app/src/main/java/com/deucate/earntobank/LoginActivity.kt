package com.deucate.earntobank

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val signIn = 69
    private val util = Util(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser!=null){
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
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
                    signInToFirebase(account!!)
                } catch (e: ApiException) {
                    util.showAlertDialog("Error", e.localizedMessage)
                } catch (e: NullPointerException) {
                    util.showAlertDialog("Error", e.localizedMessage)
                }
            }
        }
    }

    private fun signInToFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startHomeActivity()
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
