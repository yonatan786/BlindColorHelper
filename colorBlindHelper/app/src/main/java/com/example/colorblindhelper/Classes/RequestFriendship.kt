package com.example.colorblindhelper

enum class Status{WAITING,FRIENDS,REJECTED}
class RequestFriendship (val status:Status,val userSend:String,val userGet:String){
        constructor() : this(Status.WAITING,"","")

}