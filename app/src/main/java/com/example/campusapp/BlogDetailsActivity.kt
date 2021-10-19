package com.example.campusapp

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_blog_details.*

import java.lang.String


class BlogDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)
        loadUI()

    }


    fun loadUI(){
        textAuthor.setText("Mehedi Hasan")
        blogTitleTextView.setText("Hello from Sydney!")
        imageAvatar.setImageResource(R.drawable.avatar)
        dateTextView.setText("20 October, 2021")
        blogImageView.setImageResource(R.drawable.sydney_image)
        textRating.setText("4.4")
        textViews.setText("(420 views)")
        textDescription.setText("Australia is one of the most popular travel destinations in the world.")
        blogRatingBar.setRating(4.4f)
        backIcon.setOnClickListener { v->finish() }

    }
}