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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
   // private val adapter = MainAdapter()

    private val adapter = MainAdapter { blog ->
        BlogDetailsActivity.start(this, blog)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.layoutManager = LinearLayoutManager(this) // 2
        binding.recyclerView.adapter = adapter // 3
        binding.refreshLayout.setOnRefreshListener {
            loadData()
        }
        loadData()
    }

    private fun loadData() {
        binding.refreshLayout.isRefreshing = true // 2
        BlogHttpClient.loadBlogArticles(
            onSuccess = { blogList: List<Blog> ->
                runOnUiThread {
                    binding.refreshLayout.isRefreshing = false // 3
                    adapter.submitList(blogList)
                }
            },
            onError = {
                runOnUiThread {
                    binding.refreshLayout.isRefreshing = false // 3
                    showErrorSnackbar()
                }
            }
        )
    }

    private fun showErrorSnackbar() {
        Snackbar.make(binding.rootActivity,
            "Error during loading blog articles.\nCheck Your internet connection.", Snackbar.LENGTH_INDEFINITE).run {
            setActionTextColor(resources.getColor(R.color.orange_500))
            setAction("Retry") {
                loadData()
                dismiss()
            }
        }.show()
    }
}