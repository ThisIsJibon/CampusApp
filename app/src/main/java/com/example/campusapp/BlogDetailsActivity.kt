package com.example.campusapp

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.campusapp.http.Blog
import com.example.campusapp.http.BlogHttpClient
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_blog_details.*

import java.lang.String

private const val IMAGE_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/" +
        "3436e16367c8ec2312a0644bebd2694d484eb047/images/sydney_image.jpg"
private const val AVATAR_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/" +
        "3436e16367c8ec2312a0644bebd2694d484eb047/avatars/avatar1.jpg"

class BlogDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)

        loadData()
    }

    private fun loadData() {
        BlogHttpClient.loadBlogArticles( // 1
            onSuccess = { list: List<Blog> ->
                runOnUiThread { showData(list[0]) } // 3
            },
            onError = {
                runOnUiThread { showErrorSnackBar() }
            }
        )
    }
    fun showErrorSnackBar(){
        Snackbar.make(activityBlogDetailsView,
            "Error during loading blog articles", Snackbar.LENGTH_INDEFINITE).run {
            setActionTextColor(resources.getColor(R.color.orange_500))
            setAction("Retry") {
                loadData()
                dismiss()
            }
        }.show()
    }

    private fun showData(blog: Blog) {
        blogDetailsProgressBar.visibility= View.GONE

        blogTitleTextView.text = blog.title
        dateTextView.text = blog.date
        textAuthor.text = blog.author.name
        textRating.text = blog.rating.toString()
        textViews.text = String.format("(%d views)", blog.views)
        textDescription.text = blog.description
        blogRatingBar.rating = blog.rating

        Glide.with(this)
            .load(blog.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(blogImageView)
        Glide.with(this)
            .load(blog.author.avatar)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageAvatar)
    }

    fun loadUI(){

        Glide.with(this)
            .load(IMAGE_URL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(blogImageView)

        Glide.with(this)
            .load(AVATAR_URL)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageAvatar)

        textAuthor.setText("Mehedi Hasan")
        blogTitleTextView.setText("Hello from Sydney!")
        dateTextView.setText("20 October, 2021")
        textRating.setText("4.4")
        textViews.setText("(420 views)")
        textDescription.setText("Australia is one of the most popular travel destinations in the world.")
        blogRatingBar.setRating(4.4f)
        backIcon.setOnClickListener { v->finish() }

    }
}