package com.example.colorblindhelper.Classes

import com.example.colorblindhelper.UserModel

class CurrentUser(val user:UserModel)
{
    companion object {
        @JvmStatic lateinit var instance: CurrentUser

    }
    init {
        instance = this
    }
}