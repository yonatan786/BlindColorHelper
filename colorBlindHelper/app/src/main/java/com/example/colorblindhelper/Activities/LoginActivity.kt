package com.example.colorblindhelper.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.colorblindhelper.R
import com.example.colorblindhelper.getUserName
import com.example.colorblindhelper.updateBlindType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging


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
            getUserName(this)?.let { LogIn(it,this) }
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
            getUserName(this)?.let { LogIn(it, this) }
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            getUserName(applicationContext)?.let { LogIn(it, this, true) }
        } catch (e: ApiException) {
            Toast.makeText(applicationContext,"the connection failed",Toast.LENGTH_SHORT).show()
            Toast.makeText(applicationContext,"signInResult:failed code=" + e.getStatusCode(),Toast.LENGTH_SHORT).show()
        }
    }


    private fun LogIn(userName: String, activity: Activity, isChecking:Boolean=false) {
        val rootRef = Firebase.firestore.collection("users").whereEqualTo("userName", userName).addSnapshotListener{ snapshot, e ->
            if (!snapshot?.isEmpty!!){
                updateBlindType(snapshot.documents[0].get("blindType").toString(),this)
                if(snapshot.documents[0].get("blindType") == "UNCLASSIFIED") {
                    showNotExistTestResultDialog(activity)
                }
                else{
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                }
            }
            else if(isChecking)
            {
                val intent = Intent(this, Register_Activity::class.java)
                intent.putExtra("requestCode",0)
                startActivityForResult(intent, RC_REGISTER)
            }
        };
        val userToken = FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (!TextUtils.isEmpty(token)) {
                val token_t = hashMapOf("userToken" to token.toString())
                Firebase.firestore.collection("tokens")
                    .document(getUserName(applicationContext).toString()).set(token_t)
            }
        }
    }



    private fun showNotExistTestResultDialog(activity: Activity)
    {
        val dialog : Dialog = Dialog(this)
        dialog.setContentView(R.layout.result)
        dialog.findViewById<TextView>(R.id.resText).text = "You haven't completed the test!"
        dialog.findViewById<TextView>(R.id.result).text = "Color Blind Test"
        dialog.findViewById<Button>(R.id.btnPopup).text = "Go Now"
        dialog.findViewById<Button>(R.id.btnSkip).visibility=View.VISIBLE
        dialog.findViewById<Button>(R.id.btnPopup).setOnClickListener(View.OnClickListener{
            dialog.dismiss()
            val intent = Intent(activity, TestActivity::class.java)
            activity.startActivity(intent)
        })
        dialog.findViewById<Button>(R.id.btnSkip).setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        })
        dialog.show()
    }
}

