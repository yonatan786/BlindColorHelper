package com.example.colorblindhelper

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpTabs()
    }
    private fun setUpTabs()
    {
        val adapter = ViewPageAdapter(supportFragmentManager,3)
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = adapter
        val tabs = findViewById<TabLayout>(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_baseline_add_a_photo_24)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_baseline_settings_24)
        tabs.getTabAt(0)?.setIcon(R.drawable.ic_baseline_find_replace_24)


    }
}