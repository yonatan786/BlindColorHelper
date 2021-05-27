package com.example.colorblindhelper.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.colorblindhelper.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class viewOtherProfileActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    View.OnClickListener {
    private var userName:String? = null
    var btnRequestFriend : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        userName = intent.getStringExtra("userNameProfile")
        showProfileGridView(userName!!,findViewById<GridView>(R.id.gradeView),
            applicationContext,this)
        findViewById<TextView>(R.id.tvUserName)?.text = userName
        findViewById<TextView>(R.id.tvChangeProfilePhoto).visibility = View.GONE
        findViewById<GridView>(R.id.gradeView)?.onItemClickListener = this
        btnRequestFriend = findViewById(R.id.btnRequestFriend)
        btnRequestFriend?.visibility = View.VISIBLE
        btnRequestFriend?.setOnClickListener(this)
        val ImgViewProfile = findViewById<ImageView>(R.id.ImgViewProfile)
        downloadImgViewProfile(applicationContext, userName!!,ImgViewProfile)
        updateStatus()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position).toString().split("/")
        showDialog(position, item[item.size-1],this,this,userName!!)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.itemDetails)?.isVisible   = false
        menu?.findItem(R.id.itemNewTest)?.isVisible   = false
        menu?.findItem(R.id.itemLog_out)?.isVisible   = false
        menu?.findItem(R.id.itemGoBack)?.isVisible   = true
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemGoBack -> {
                val returnIntent = Intent()
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish()
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        if(v == btnRequestFriend)
        {
                sendRequest()

        }
    }

    private fun sendRequest() {
        val db = Firebase.firestore
        val request = RequestFriendship(
            Status.WAITING,
            getUserName(applicationContext)!!, userName!!,
        )
        db.collection("requests").document(userName!!).collection("newRequests").document(
            getUserName(applicationContext)!!).set(request)
            .addOnSuccessListener { documentReference ->
                btnRequestFriend?.setBackgroundColor(Color.WHITE)
                btnRequestFriend?.setTextColor(Color.BLACK)
                btnRequestFriend?.text = "Waiting..."
                //TODO("adding notification")
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext,"Failed to upload the details",Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateStatus()
    {
        val rootRef = Firebase.firestore.collection("requests").document(userName!!).collection("newRequests").document(
            getUserName(applicationContext)!!).addSnapshotListener { snapshot, e ->
                if(snapshot?.exists() == true)
                {
                    when(snapshot.get("status")){
                        "WAITING" -> changeButtonStatus("Waiting...")
                        "FRIENDS" -> changeButtonStatus("Friends")
                    }
                }
        }
        val checkRef = Firebase.firestore.collection("requests").document(getUserName(applicationContext)!!).collection("newRequests").document(
            userName!!).addSnapshotListener { snapshot, e ->
            if(snapshot?.exists() == true)
            {
                when(snapshot.get("status")) {
                    "WAITING" -> showAcceptRejectView(userName!!)
                }
            }
        }
    }

    private fun showAcceptRejectView(userName : String) {
        btnRequestFriend?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.layoutRequest)?.visibility = View.VISIBLE
        findViewById<Button>(R.id.btnReject).setOnClickListener{
            findViewById<LinearLayout>(R.id.layoutRequest)?.visibility = View.GONE
            RejectFriendRequest(applicationContext,userName, getUserName(applicationContext)!!)
            changeButtonStatus("Ask to be a friends")
        }
        findViewById<Button>(R.id.btnAccept).setOnClickListener{
            findViewById<LinearLayout>(R.id.layoutRequest)?.visibility = View.GONE
            AcceptFriendRequest(applicationContext,userName, getUserName(applicationContext)!!)
            changeButtonStatus("Friends")
        }
    }



    private fun changeButtonStatus(stringText :String)
    {
        btnRequestFriend?.visibility = View.VISIBLE
        btnRequestFriend?.setBackgroundColor(Color.WHITE)
        btnRequestFriend?.setTextColor(Color.BLACK)
        btnRequestFriend?.text = stringText
        btnRequestFriend?.isEnabled = false

    }
}