package com.example.colorblindhelper.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.colorblindhelper.R
import com.example.colorblindhelper.Classes.ViewPageAdapter
import com.example.colorblindhelper.getUserName
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = getIntent()
        val tab = intent.getIntExtra("tab",2)
        setUpTabs(tab)
    }

    private fun setUpTabs(tabNumber: Int) {
        val adapter = ViewPageAdapter(supportFragmentManager, 4)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(3)?.setIcon(R.drawable.ic_baseline_add_a_photo)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_baseline_profile)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_baseline_search_24)
        tabs.getTabAt(0)?.setIcon(R.drawable.ic_baseline_find_replace_24)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val tab = tabLayout.getTabAt(tabNumber) // Count Starts From 0
        tab!!.select()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        menu?.findItem(R.id.itemFriends)?.isVisible   = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemLog_out -> {
                val userName = getUserName(applicationContext)
                val token = hashMapOf("userToken" to "")
                Firebase.firestore.collection("tokens").document(userName!!).set(token)
                mGoogleSignInClient?.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.itemDetails -> {
                val intent = Intent(this, Register_Activity::class.java)
                intent.putExtra("requestCode",1)
                startActivity(intent)
            }
            R.id.itemNewTest -> {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }
            R.id.itemFriends -> {
                val intent = Intent(this, RequestActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

}
