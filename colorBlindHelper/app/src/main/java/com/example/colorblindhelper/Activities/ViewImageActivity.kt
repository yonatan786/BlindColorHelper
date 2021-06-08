package com.example.colorblindhelper.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.colorblindhelper.Classes.commentModel
import com.example.colorblindhelper.Classes.notificationModel
import com.example.colorblindhelper.R
import com.example.colorblindhelper.downloadImgViewProfile
import com.example.colorblindhelper.getUserName
import com.example.colorblindhelper.viewImg
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.w3c.dom.Text

class ViewImage : AppCompatActivity(), View.OnClickListener {
    private var fileName : String? = null
    private var userName : String? = null
    private var rvCommentsList: RecyclerView? = null
    private var tvSend: TextView? = null
    private var etComment: EditText? = null
    private var uname: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        fileName = intent.getStringExtra("fileName")
        userName = intent.getStringExtra("username")
        tvSend = findViewById(R.id.tvSend)
        etComment = findViewById(R.id.etComment)
        uname = findViewById(R.id.uname)
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/posts/$userName")
        viewImg(applicationContext, "images/posts/$userName/$fileName",findViewById<ImageView>(R.id.imgViewPost))
        findViewById<LinearLayout>(R.id.dialogLayer).visibility = View.GONE
        findViewById<LinearLayout>(R.id.layoutComment).visibility = View.VISIBLE
        uname!!.visibility = View.VISIBLE
        uname!!.text = "$userName"
        uname!!.setOnClickListener(View.OnClickListener {
            if(userName != getUserName(applicationContext)) {
                val intent = Intent(applicationContext, viewOtherProfileActivity::class.java)
                intent.putExtra("userNameProfile", "$userName")
                startActivity(intent)
            }
        })

        rvCommentsList = findViewById<RecyclerView>(R.id.rvCommentsList)
        tvSend?.setOnClickListener(this)
        firebaseUpdate()
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
                val isNotification = intent.getBooleanExtra("notification",false)
                if(isNotification ) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    return false
                }
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish()
            }
        }
        return true
    }
    private fun firebaseUpdate() {
        val query  = FirebaseFirestore.getInstance().collection("photos/$userName/$fileName/").orderBy("timeStamp")
        val options = FirestoreRecyclerOptions.Builder<commentModel>()
            .setQuery(query, commentModel::class.java)
            .setLifecycleOwner(this)
            .build()
        val adapter = object : FirestoreRecyclerAdapter<commentModel, ViewCommentHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewCommentHolder {
                return ViewCommentHolder(
                    LayoutInflater.from(parent.context)
                    .inflate(R.layout.comment_row, parent, false))
            }

            override fun onBindViewHolder(holder: ViewCommentHolder, position: Int, model: commentModel) {
                holder.tvUserName?.text = model.getUserName()
                downloadImgViewProfile(applicationContext, model.getUserName(),holder.imgViewProfile!!)
                holder.itemView.setOnClickListener {
                    if(model.getUserName() != getUserName(applicationContext)) {
                        val intent = Intent(applicationContext, viewOtherProfileActivity::class.java)
                        intent.putExtra("userNameProfile", model.getUserName())
                        startActivity(intent)
                    }
                }
                holder.tvComment?.text = model.getTextComment()
            }
        }
        rvCommentsList?.layoutManager = LinearLayoutManager(this);
        rvCommentsList?.adapter = adapter
    }

    override fun onClick(v: View?) {
        if(v == tvSend)
        {
            val commentText = etComment?.text.toString()
            etComment?.setText("")
            val userSend = getUserName(applicationContext)
            uploadCommentToFirebase(commentText,userSend!!, userName!!, fileName!!,applicationContext)
        }
    }

    private fun uploadCommentToFirebase(
        commentText: String,
        userSend: String,
        userName: String,
        fileName: String,
        context:Context
    ) {
       FirebaseFirestore.getInstance().collection("photos/$userName/$fileName/").add(commentModel(userSend,commentText,
           ))
            .addOnSuccessListener { documentReference ->
                firebaseUpdate()
//                val sp = applicationContext.getSharedPreferences("notificationSwitch", MODE_PRIVATE)
//                val switchState = sp.getBoolean("notifySwitch", true)
                Firebase.firestore.collection("users").document(userName).get()
                    .addOnSuccessListener { docRef ->
                        if (docRef.getBoolean("switchStatus") == true) {
                            if (userName != getUserName(applicationContext).toString()) {
                                val reqNotification = notificationModel(
                                    getUserName(applicationContext).toString() + " has commented on your post.",
                                    "New Comment",
                                    userName,
                                    fileName,
                                    getUserName(applicationContext).toString()
                                )
                                Firebase.firestore.collection("tokens").document(userName).get()
                                    .addOnSuccessListener { doc ->
                                        if (doc["userToken"].toString().isNotEmpty()) {
                                            Firebase.firestore.collection("notifications")
                                                .document(getUserName(applicationContext)!!)
                                                .set(reqNotification)
                                                .addOnSuccessListener {

                                                }
                                        }
                                    }
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context,"Failed to upload comment", Toast.LENGTH_SHORT).show()
            }

    }
}
class ViewCommentHolder(view: View) : RecyclerView.ViewHolder(view) {


    public var tvUserName : TextView? = null
    public var imgViewProfile: ImageView? = null
    public var tvComment: TextView? = null
    init{
        tvUserName = view.findViewById(R.id.tvUserName)
        imgViewProfile = view.findViewById(R.id.ImgViewProfile)
        tvComment = view.findViewById(R.id.tvComment)
    }
}