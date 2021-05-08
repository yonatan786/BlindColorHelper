package com.example.colorblindhelper

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ViewImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        val fileName = intent.getStringExtra("fileName")
        val userName = intent.getStringExtra("username")

        val storageRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images/posts/$userName")
        viewImg(applicationContext,storageRef, fileName!!,findViewById<ImageView>(R.id.imgViewPost))
        findViewById<LinearLayout>(R.id.dialogLayer).visibility = View.GONE


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