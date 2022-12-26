package com.example.campusappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.campusappdemo.databinding.ActivityMarketPlaceCardCategorySectionBinding
import java.lang.reflect.Array
import java.util.*

class MarketPlaceCardCategorySectionActivity : AppCompatActivity() {



    private lateinit var binding: ActivityMarketPlaceCardCategorySectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMarketPlaceCardCategorySectionBinding.inflate(layoutInflater)

        setContentView(binding.root)

        backButton()




    }



    private fun backButton(){

        binding.categorySectionBackButton.setOnClickListener {

            ///val intent =Intent(this@MarketPlaceCardCategorySectionActivity,MarketPlaceCardActivity::class.java)
            ///startActivity(intent)
            finish()
        }

    }


}