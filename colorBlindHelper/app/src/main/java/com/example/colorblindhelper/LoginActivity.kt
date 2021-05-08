package com.example.colorblindhelper

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


var signInButton : SignInButton? = null
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestEmail()
    .build()
var mGoogleSignInClient : GoogleSignInClient? = null
val RC_SIGN_IN = 100
val RC_REGISTER = 300
class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton?.setSize(SignInButton.SIZE_STANDARD)
        signInButton?.setOnClickListener(this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null /*adding check */ ) {
//            saveUserID(this, account.id)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onClick(v: View?) {
        if(v == signInButton)
        {
            val signInIntent: Intent = mGoogleSignInClient?.signInIntent!!
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        if(requestCode == RC_REGISTER && resultCode == RESULT_OK) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val intent = Intent(this, Register_Activity::class.java)
            intent.putExtra("requestCode",0)
            startActivityForResult(intent, RC_REGISTER)
        } catch (e: ApiException) {
            //updateUI(null)
        }
    }




/*    private fun saveUserID(context: Context, account : String?)
    {
        val pref = applicationContext.getSharedPreferences("userID", 0) // 0 - for private mode
        val editor = pref.edit()
        editor.putString("userID",account)
        editor.apply();
    }*/
}

class settingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}