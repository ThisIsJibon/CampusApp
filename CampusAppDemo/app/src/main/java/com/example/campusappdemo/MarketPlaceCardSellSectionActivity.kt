package com.example.campusappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityMarketPlaceCardSellSectionBinding

class MarketPlaceCardSellSectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMarketPlaceCardSellSectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        binding= ActivityMarketPlaceCardSellSectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        backButton()

        start()

    }


    private fun backButton(){

        binding.marketPlaceCardSellSectionBackButton.setOnClickListener {
            finish()
        }

    }

    private fun start(){

        val items = listOf("Womens Clothing and Shoes", "Mens Clothing and Shoes", "Electronics", "Health and Beauty","Musical Instrument","Sporting Goods")
        val adapter = ArrayAdapter(this, R.layout.event_item, items)
        (binding.marketPlaceCardSellSectionSellCategoryLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        val items1 = listOf("New", "Used-Like New", "Used-Good", "Used-Fair")
        val adapter1 = ArrayAdapter(this, R.layout.event_item, items1)
        (binding.marketPlaceCardSellSectionSellConditionLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter1)



    }

}