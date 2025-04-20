package com.example.solutionchallenge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.solutionchallenge.NicknameSetupActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ✅ 요게 핵심!!!
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google Sign-In Button click listener
        findViewById<Button>(R.id.googleSignInButton).setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("OnboardingActivity", "onActivityResult 호출됨") // ✅ 추가

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("OnboardingActivity", "구글 계정 가져옴: ${account?.email}") // ✅ 추가
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.e("OnboardingActivity", "구글 로그인 실패: ${e.message}") // ✅ 추가
            }
        }
    }


    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // ✅ 로그인 성공 로그
                    Log.d("OnboardingActivity", "로그인 성공!")
                    startActivity(Intent(this, NicknameSetupActivity::class.java))
                } else {
                    // ❌ 로그인 실패 로그
                    Log.e("OnboardingActivity", "로그인 실패: ${task.exception}")
                }
            }
    }


    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
