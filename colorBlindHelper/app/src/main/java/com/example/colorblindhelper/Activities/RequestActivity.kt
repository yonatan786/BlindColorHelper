package com.example.colorblindhelper.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorblindhelper.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


class RequestActivity : AppCompatActivity() {
    var rvRequestsList : RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        rvRequestsList = findViewById(R.id.rvRequestsList)
        firebaseRequestList()
    }




    private fun firebaseRequestList() {
        val query = FirebaseFirestore.getInstance().collection("requests/"+ getUserName(applicationContext) +"/newRequests")
            .whereNotEqualTo("status", "FRIENDS")

        val options = FirestoreRecyclerOptions.Builder<RequestFriendship>()
            .setQuery(query, RequestFriendship::class.java)
            .setLifecycleOwner(this)
            .build()
        val adapter = object : FirestoreRecyclerAdapter<RequestFriendship, ViewHolderRequest>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRequest {
                return ViewHolderRequest(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.request_row, parent, false)
                )
            }
            override fun onBindViewHolder(holder: ViewHolderRequest, position: Int, model: RequestFriendship) {
                holder.tvUserName?.text = model.userSend
                downloadImgViewProfile(applicationContext,model.userSend,holder.imgViewProfile!!)
                holder.itemView.setOnClickListener {
                    val intent = Intent(applicationContext, viewOtherProfileActivity::class.java)
                    intent.putExtra("userNameProfile", model.userSend)
                    startActivity(intent)
                }
                holder.btnReject?.setOnClickListener{
                    RejectFriendRequest(applicationContext,model.userSend,
                        getUserName(applicationContext)!!)
                }
                holder.btnAccept?.setOnClickListener{
                    AcceptFriendRequest(applicationContext,model.userSend,
                        getUserName(applicationContext)!!)
                }
            }
            override fun onDataChanged() {
                if(itemCount == 0) {
                    findViewById<TextView>(R.id.tvNoRequest).text = "There aren't requests..."
                }
                else {
                    findViewById<TextView>(R.id.tvNoRequest).text = "requests"
                }
            }
        }

        rvRequestsList?.layoutManager = LinearLayoutManager(this);
        rvRequestsList?.adapter = adapter
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
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish()
        return true
    }
}
class ViewHolderRequest(view: View) : RecyclerView.ViewHolder(view) {


    public var tvUserName : TextView? = null
    public var imgViewProfile: ImageView? = null
    public var btnAccept: Button? = null
    public var btnReject: Button? = null
    init{
        tvUserName = view.findViewById(R.id.tvUserName)
        imgViewProfile = view.findViewById(R.id.ImgViewProfile)
        btnAccept = view.findViewById(R.id.btnAccept)
        btnReject = view.findViewById(R.id.btnReject)
    }
}
