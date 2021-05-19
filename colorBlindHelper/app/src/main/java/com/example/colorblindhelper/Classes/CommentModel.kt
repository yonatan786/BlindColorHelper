package com.example.colorblindhelper.Classes;

class commentModel(private val userName:String, private val textComment:String){

    constructor() : this("", "")

    public fun getUserName():String{
        return userName
    }
    public fun getTextComment():String{
        return textComment
    }
}