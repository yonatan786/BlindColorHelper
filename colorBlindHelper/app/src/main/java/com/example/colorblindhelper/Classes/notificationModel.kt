package com.example.colorblindhelper.Classes;

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class notificationModel(val content:String,val title:String,val userId: String, val imageFile: String, val myUName: String, @ServerTimestamp
private val timeStamp: Timestamp? = null){


    constructor() : this("", "","", "", "",null)
    public fun getTimeStamp():Timestamp?{
        return timeStamp
    }
}