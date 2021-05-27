package com.example.colorblindhelper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

enum class Status{WAITING,FRIENDS,REJECTED}
class RequestFriendship (val status:Status,val userSend:String,val userGet:String, @ServerTimestamp
private val timeStamp: Timestamp? = null) {
        constructor() : this(Status.WAITING, "", "", null)

        public fun getTimeStamp(): Timestamp? {
                return timeStamp
        }
}