package com.example.campusappdemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentsModel(val authorID : String,
                         val storyID : String,
                         val id : String,
                         val comment : String,
                         val timestamp: Long
):Parcelable{
    constructor():this("","","","",0)
}