package com.example.campusapp

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.campusapp.databinding.ActivityBlogDetailsBinding
import com.example.campusapp.databinding.ActivityMainBinding
import com.example.campusapp.http.Blog
import com.example.campusapp.http.BlogHttpClient
import com.google.android.material.snackbar.Snackbar

import java.lang.String

private const val IMAGE_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/" +
        "3436e16367c8ec2312a0644bebd2694d484eb047/images/sydney_image.jpg"
private const val AVATAR_URL = "https://bitbucket.org/dmytrodanylyk/travel-blog-resources/raw/" +
        "3436e16367c8ec2312a0644bebd2694d484eb047/avatars/avatar1.jpg"

class BlogDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlogDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)

        binding = ActivityBlogDetailsBinding.inflate(layoutInflater) // 1
        setContentView(binding.root)

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
        Snackbar.make(binding.activityBlogDetailsView,
            "Error during loading blog articles", Snackbar.LENGTH_INDEFINITE).run {
            setActionTextColor(resources.getColor(R.color.orange_500))
            setAction("Retry") {
                loadData()
                dismiss()
            }
        }.show()
    }

    private fun showData(blog: Blog) {
        binding.blogDetailsProgressBar.visibility= View.GONE

        binding.blogTitleTextView.text = blog.title
        binding.dateTextView.text = blog.date
        binding.textAuthor.text = blog.author.name
        binding.textRating.text = blog.rating.toString()
        binding.textViews.text = String.format("(%d views)", blog.views)

        binding.textDescription.text = Html.fromHtml(blog.description)

        binding.textDescription.text = Html.fromHtml(blog.description )

        binding.blogRatingBar.rating = blog.rating

        Glide.with(this)
            .load(blog.image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.blogImageView)
        Glide.with(this)
            .load(blog.author.avatar)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageAvatar)
    }

    fun loadUI(){

        Glide.with(this)
            .load(IMAGE_URL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.blogImageView)

        Glide.with(this)
            .load(AVATAR_URL)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageAvatar)

        binding.textAuthor.setText("Mehedi Hasan")
        binding.blogTitleTextView.setText("Hello from Sydney!")
        binding.dateTextView.setText("20 October, 2021")
        binding.textRating.setText("4.4")
        binding.textViews.setText("(420 views)")
        binding.textDescription.setText("Australia is one of the most popular travel destinations in the world.")
        binding.blogRatingBar.setRating(4.4f)
        binding.backIcon.setOnClickListener { v->finish() }

    }

    fun backIconAction(view: View) {
        finish()
    }
}