package com.example.campusappdemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoriesModel(val authorID : String,
                        val id : String,
                        val title :String,
                        val numberOfComment: Int,
                        val numberOfUpvote: Int,
                        val description : String,
                        val timestamp: Long,
                        val imageURL : String
                        ):Parcelable{
    constructor():this("","","",0,0,"",0,"no_url")
}