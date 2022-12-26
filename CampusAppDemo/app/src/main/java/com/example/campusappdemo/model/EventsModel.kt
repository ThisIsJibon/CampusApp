package com.example.campusappdemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventsModel(val eventID: String,
                       val eventTitle :String,
                       val eventDesc: String,
                       val authorID: String,
                       val duration : String,
                       val startTime: String,
                       val startDate:String,
                       val going : Int,
                       val location : String,
                       val eventPicURL : String
                        ) : Parcelable {
    constructor():this("","","","0","0","","",0,"","no_url")
}