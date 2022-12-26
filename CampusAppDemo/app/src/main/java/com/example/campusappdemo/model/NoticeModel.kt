package com.example.campusappdemo.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class NoticeModel(val noticeID: String,
                       val noticeTitle :String,
                       val noticeDesc: String,
                       val authorID: String,
                       val attachmentLink : String,
                       val deadline : String,
                       val timestamp : Long,
) : Parcelable {
    constructor():this("","","","0","no attachment","no deadline",0)
}