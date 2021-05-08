package com.example.colorblindhelper

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val returnIntent = Intent()
        setResult(Activity.RESULT_OK, returnIntent);
        finish()
    }
}