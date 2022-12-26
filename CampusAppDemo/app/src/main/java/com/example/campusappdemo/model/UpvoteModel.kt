package com.example.campusappdemo.model

data class UpvoteModel(val storyID: String, val authorID: String) {
    constructor():this("","")
}