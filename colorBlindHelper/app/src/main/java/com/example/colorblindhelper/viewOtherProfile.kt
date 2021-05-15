package com.example.colorblindhelper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class viewOtherProfile : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var userName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        userName = intent.getStringExtra("userNameProfile")
        getfileNameList(userName!!,findViewById<GridView>(R.id.gradeView),
            applicationContext,this)
        findViewById<TextView>(R.id.tvUserName)?.text = userName
        findViewById<TextView>(R.id.tvChangeProfilePhoto).visibility = View.GONE
        findViewById<GridView>(R.id.gradeView)?.onItemClickListener = this
        val ImgViewProfile = findViewById<ImageView>(R.id.ImgViewProfile)
        downloadImgViewProfile(applicationContext, userName!!,ImgViewProfile)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        showDialog(position, item as String?,this,this,userName!!)
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
}