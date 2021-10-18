package com.example.campusapp.http


class BlogData {
    val data: List<Blog>? = null
        get() = field ?: ArrayList()
}
