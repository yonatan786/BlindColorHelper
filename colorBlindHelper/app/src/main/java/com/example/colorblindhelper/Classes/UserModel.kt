package com.example.colorblindhelper

import com.google.firebase.database.Exclude
import java.util.*
enum class ClassifyBlindness{NORMAL,RED_BLIND,GREEN_BLIND,UNCLASSIFIED,BLACK_WHITE_BLIND}
class UserModel(private val userName:String, private val isGlasses: Boolean, private val gender: Gender?, private val birthDate: String, private val fullName:String, private val switchStatus: Boolean = true, private var typeBlind:ClassifyBlindness = ClassifyBlindness.UNCLASSIFIED,
){


    constructor() : this("", true,null, "","")

    public fun getUserName():String{
        return userName
    }
    public fun getFullName():String{
        return fullName
    }
    public fun getisGlasses():Boolean{
        return isGlasses
    }
    public fun getGender():Gender?{
        return gender
    }
    public fun getBirthDate(): String {
        return birthDate
    }
    public fun getSwitchStatus(): Boolean {
        return switchStatus
    }
    public fun getBlindType(): ClassifyBlindness {
        return typeBlind
    }
    public fun setType(type:ClassifyBlindness) {
        typeBlind = type
    }
}