package com.example.campusappdemo

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.campusappdemo.databinding.ActivityProfileDetailsBinding
import com.example.campusappdemo.databinding.ActivityStoryDetailsBinding
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileDetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val author = intent.getParcelableExtra<UserModel>(StoryDetailsActivity.AUTHORID)
        if (author != null) {
            setUserInfo(author)
        }
        if (author != null) {
            checkFollowingCase(author)
        }
        binding.profileDetailsFollowButton.setOnClickListener {
            if(binding.profileDetailsFollowButton.text.toString()=="Follow") followUser(author!!)
            else unfollowUser(author!!)
        }
    }

    private fun checkFollowingCase(author: UserModel){
        val uid = FirebaseAuth.getInstance().uid.toString()
        var ref = FirebaseDatabase.getInstance().getReference("/following_activity")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val vitore = it.key.toString()
                    if(vitore==uid){
                        val reff = it.ref
                        reff.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach{
                                    val x =it.value.toString()
                                    if(x==author.uid)
                                        binding.profileDetailsFollowButton.text="Unfollow"
                                }
                            }
                        })

                    }
                }
            }
        })
    }

    private fun unfollowUser(author: UserModel){

    }

    private fun followUser(author: UserModel){

        val uid = FirebaseAuth.getInstance().uid.toString()
        var ref = FirebaseDatabase.getInstance().getReference("/following_activity/$uid")
        ref.setValue(followingModel(author.uid))
        ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        ref.child(uid).child("followingNum").setValue(user.followingNum+1)
                    }
                    if(user!=null && user.uid==author.uid){
                        ref.child(user.uid).child("followerNum").setValue(user.followerNum+1)
                    }
                }
            }
        })
        binding.profileDetailsFollowButton.text="Unfollow"
    }
    private fun setUserInfo(author : UserModel) {

        val ref = FirebaseDatabase.getInstance().getReference("/user")
        val uid = author.uid
        if (uid.isNotEmpty()) {
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    binding.profileDetailsNameTextView.text = user?.name
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

            if(author.profilePicURL!="no_url" && author.profilePicURL!="" )
                Glide.with(this).load(author.profilePicURL).circleCrop()
                .into(binding.profileDetailsProfileImageView)
        }
        binding.profileDetailsFollowingTextView.text = author.followingNum.toString()
        binding.profileDetailsFollowersTextView.text = author.followerNum.toString()
        binding.profileDetailsPostsTextView.text = author.totalPost.toString()
        binding.profileDetailsStoriesPostedTextview.text = author.storiesPosted.toString() + "   stories"
        binding.profileDetailsEventsPostedTextview.text = author.eventsPosted.toString() + "   events"
        binding.profileDetailsNoticePostedTextview.text = author.noticePosted.toString() + "   notice"
    }
}
class followingModel(val followingID : String){
    constructor():this("")
}