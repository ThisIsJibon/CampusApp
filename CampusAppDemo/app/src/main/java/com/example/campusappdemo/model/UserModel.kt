package com.example.campusappdemo.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(val name: String,val email:String,val uid: String,val totalPost : Int,val storiesPosted : Int,val eventsPosted : Int,val noticePosted : Int,val followingNum : Int,val followerNum : Int,val profilePicURL :String) : Parcelable {
    constructor():this("","","",0,0,0,0,0,0,"")
}
