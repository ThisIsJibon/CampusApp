package com.example.campusapp.http

import com.google.gson.Gson

import okhttp3.OkHttpClient
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class BlogHttpClient {

    val INSTANCE = BlogHttpClient()
    
    private val BASE_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/"
    private val BLOG_ARTICLES_URL =
        BASE_URL + "8550ef2064bf14fcf3b9ff322287a2e056c7e153/blog_articles.json"

    private var executor: Executor? = null
    private var client: OkHttpClient? = null
    private var gson: Gson? = null

    private fun BlogHttpClient() {
        executor = Executors.newFixedThreadPool(4)
        client = OkHttpClient()
        gson = Gson()
    }
}