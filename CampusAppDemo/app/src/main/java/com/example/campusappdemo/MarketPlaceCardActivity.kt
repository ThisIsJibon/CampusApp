package com.example.campusappdemo

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityMarketPlaceCardBinding

class MarketPlaceCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketPlaceCardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMarketPlaceCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        backButton()

        createPost()

        category()

        searchItem()


    }


    private fun backButton(){


        binding.marketPlaceCardCancelImageView.setOnClickListener {

            ///val intent= Intent(this@MarketPlaceCardActivity,MainActivity::class.java)
            ///startActivity(intent)
            finish()

        }


    }

    private fun createPost(){

        binding.marketPlaceCardSellLayout.setOnClickListener {


            val intent =Intent(this@MarketPlaceCardActivity,MarketPlaceCardSellSectionActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun category(){

        binding.marketPlaceCardCategoriesLayout.setOnClickListener {

            val intent=Intent(this@MarketPlaceCardActivity,MarketPlaceCardCategorySectionActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun searchItem(){

        binding.marketPlaceCardSearchButton.setOnClickListener {



        }

    }

}