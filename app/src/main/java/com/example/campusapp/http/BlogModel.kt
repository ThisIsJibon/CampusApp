package com.example.campusapp.http

data class BlogData(val data: List<Blog>)

data class Blog(
    val id: String,
    var author: Author,
    val title: String,
    val date: String,
    val image: String,
    val description: String,
    val views: Int,
    val rating: Float
){
    fun getImageUrl() = BlogHttpClient.BASE_URL + BlogHttpClient.PATH + image
}

data class Author(val name: String, val avatar: String){
    fun getAvatarUrl() = BlogHttpClient.BASE_URL + BlogHttpClient.PATH + avatar
}