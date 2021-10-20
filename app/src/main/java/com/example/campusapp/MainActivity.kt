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
        refreshLayout.setOnRefreshListener {
            loadData()
        }
        loadData()
    }

    private fun loadData() {
        refreshLayout.isRefreshing = true // 2
        BlogHttpClient.loadBlogArticles(
            onSuccess = { blogList: List<Blog> ->
                runOnUiThread {
                    refreshLayout.isRefreshing = false // 3
                    adapter.submitList(blogList)
                }
            },
            onError = {
                runOnUiThread {
                    refreshLayout.isRefreshing = false // 3
                    showErrorSnackbar()
                }
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