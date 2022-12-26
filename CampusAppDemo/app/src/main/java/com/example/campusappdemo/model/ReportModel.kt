package com.example.campusappdemo.model

import java.sql.Timestamp

data class ReportModel(val reportID: String, val reportAbout: String, val reportDesc : String, val imageURL:String ,val imgFileName: String, val videoURL : String, val videoFileName :String,val timestamp: Long) {
    constructor():this("","","","","","","",0)
}