package com.example.campusappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.AdapterView
import com.example.campusappdemo.databinding.ActivityEditProfileFragmentBinding




class EditProfileFragmentActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener{

    private lateinit var binding: ActivityEditProfileFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityEditProfileFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val bloodGroups = listOf("A+", "A-", "B+", "B-","O+","O-","AB+","AB-")

        val spinner=binding.bloodGroupSpinner

        if (spinner != null)
        {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = this



    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        binding.bloodtype.setText(p0?.getItemAtPosition(p2).toString())

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}