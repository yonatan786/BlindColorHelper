package com.example.colorblindhelper.Classes;

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
class imgModel(private val imgName:String,private val userName:String, @ServerTimestamp
private val timeStamp: Timestamp? = null){


    constructor() : this("", "",null)

    public fun getUserName():String{
        return userName
    }
    public fun getImgName():String{
        return imgName
    }
    public fun getTimeStamp():Timestamp?{
        return timeStamp
    }
}