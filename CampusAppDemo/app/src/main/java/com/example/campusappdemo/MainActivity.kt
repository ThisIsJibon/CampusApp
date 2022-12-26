package com.example.campusappdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.campusappdemo.databinding.ActivityMainBinding
import com.example.campusappdemo.fragment.DashboardFragment
import com.example.campusappdemo.fragment.NotificationFragment
import com.example.campusappdemo.fragment.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {


    private lateinit var currentFragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        supportFragmentManager.beginTransaction().replace(R.id.nav_container,DashboardFragment()).commit()
        // now create a menu
        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(bottomListener)


    }


    val bottomListener = BottomNavigationView.OnNavigationItemSelectedListener {
        // switch between ids of menu
        when(it.itemId){
            R.id.profile -> {
                currentFragment = ProfileFragment()
            }
            R.id.dashboard -> {
                currentFragment = DashboardFragment()
            }
            R.id.notification -> {
                currentFragment = NotificationFragment()
            }
        }
        supportFragmentManager.beginTransaction().replace(R.id.nav_container,currentFragment).commit()
        true
    }

}