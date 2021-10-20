/*package com.example.campusapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startActivity(Intent(this,BlogDetailsActivity::class.java))
    }
}*/

package com.example.campusapp
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusapp.databinding.ActivityMainBinding
import com.example.campusapp.http.Blog
import com.example.campusapp.http.BlogHttpClient
import com.google.android.material.snackbar.Snackbar
import com.example.campusapp.adapter.MainAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView.layoutManager = LinearLayoutManager(this) // 2
        recyclerView.adapter = adapter // 3

        loadData()
    }

    private fun loadData() {
        BlogHttpClient.loadBlogArticles(
            onSuccess = { blogList: List<Blog> ->
                runOnUiThread { adapter.submitList(blogList) }
            },
            onError = {
                runOnUiThread { showErrorSnackbar() }
            }
        )
    }

    private fun showErrorSnackbar() {
        Snackbar.make(rootActivity,
            "Error during loading blog articles", Snackbar.LENGTH_INDEFINITE).run {
            setActionTextColor(resources.getColor(R.color.orange_500))
            setAction("Retry") {
                loadData()
                dismiss()
            }
        }.show()
    }
}