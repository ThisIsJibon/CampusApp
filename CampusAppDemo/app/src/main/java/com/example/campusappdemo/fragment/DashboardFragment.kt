package com.example.campusappdemo.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.campusappdemo.*
import com.example.campusappdemo.databinding.FragmentDashboardBinding
import com.example.campusappdemo.model.UserModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding=FragmentDashboardBinding.inflate(inflater,container,false)

        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)


        binding.storyCard.setOnClickListener(){
            val intent = Intent(getActivity(), StoryCardActivity::class.java)
            startActivity(intent)
        }

        binding.postCard.setOnClickListener(){
            val intent = Intent(getActivity(), PostCardActivity::class.java)
            startActivity(intent)
        }

        binding.noticeCard.setOnClickListener(){
            val intent = Intent(getActivity(), NoticeCardActivity::class.java)
            startActivity(intent)
        }

        binding.eventCard.setOnClickListener(){
            val intent = Intent(getActivity(), EventCardActivity::class.java)
            startActivity(intent)
        }

        binding.marketPlaceCard.setOnClickListener(){
            val intent = Intent(getActivity(),MarketPlaceCardActivity::class.java)
            startActivity(intent)
        }
        binding.reportCard.setOnClickListener(){
            val intent = Intent(getActivity(),ReportCardActivity::class.java)
            startActivity(intent)
        }




        //loadUserData()
        context?.let { loadUserNameEmail(it) }




        return binding.root
    }


    private fun loadUserNameEmail(context: Context){


        if(activity==null)
            return



        val ref = FirebaseDatabase.getInstance().getReference("/user")
        val uid = FirebaseAuth.getInstance().uid.toString()
        if(uid.isNotEmpty()){
            ref.child(uid).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {


                    val user=snapshot.getValue(UserModel::class.java)
                    binding.dashboardFragmentNameTextView.text=user?.name
                    binding.dashboardFragmentEmailTextVIew.text=user?.email
                    if(user!=null){
                        if(user.profilePicURL!="no_url" && user.profilePicURL!="" )
                            Glide.with(context).load(user.profilePicURL).circleCrop()
                                .into(binding.dashboardFragmentProfileImageVIew)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        }
    }



}