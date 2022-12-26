package com.example.campusappdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.campusappdemo.databinding.ActivityEventCardCreateEventBinding

class EventCardCreateEventActivity: AppCompatActivity() {


    private lateinit var binding: ActivityEventCardCreateEventBinding


    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityEventCardCreateEventBinding.inflate(layoutInflater)

        setContentView(binding.root)

        backButton()





    }


    private fun  backButton(){

        binding.eventCardCreateEventBackButton.setOnClickListener {
            finish()
        }
    }



}