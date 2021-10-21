package com.example.campusapp.http

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler

data class BlogData(val data: List<Blog>)
@Parcelize
data class Blog(
    val id: String,
    var author: Author,
    val title: String,
    val date: String,
    val image: String,
    val description: String,
    val views: Int,
    val rating: Float
) : Parcelable {
    fun getImageUrl() = BlogHttpClient.BASE_URL + BlogHttpClient.PATH + image
}
@Parcelize
data class Author(val name: String, val avatar: String): Parcelable{
    constructor(parcel: Parcel) : this( // 1
        parcel.readString()!!,
        parcel.readString()!!)

    fun getAvatarUrl() = BlogHttpClient.BASE_URL + BlogHttpClient.PATH + avatar

    companion object : Parceler<Author> { // 3

        override fun Author.write(parcel: Parcel, flags: Int) { // 2
            parcel.writeString(name)
            parcel.writeString(avatar)
        }

        override fun create(parcel: Parcel): Author {
            return Author(parcel)
        }
    }
}