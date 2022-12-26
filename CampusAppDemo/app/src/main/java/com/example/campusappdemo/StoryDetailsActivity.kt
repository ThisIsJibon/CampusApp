package com.example.campusappdemo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.campusappdemo.databinding.ActivityStoryDetailsBinding
import com.example.campusappdemo.model.CommentsModel
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UpvoteModel
import com.example.campusappdemo.model.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*


class StoryDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailsBinding
    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.ENGLISH)
    companion object{
        val KEY="STORY_DETAILS_ACTIVITY"
        val AUTHORID="AUTHOR_ID_FROM_STORY_DETAILS"
        lateinit var storyAuthor : UserModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<StoriesModel>(StoryCardActivity.KEY)

        binding.activitivStoryDetailsCommentImageView.setOnClickListener{
            val intent = Intent(this,CommentLogActivity::class.java)
            intent.putExtra(KEY,story?.id)
            startActivity(intent)
        }
        binding.activitivStoryDetailsUpvoteImageView.setOnClickListener {
            if (story != null) {
                upvotePost(story.id)
            }
        }

        fetchStory(story?.id.toString())
        initialUiTask(story?.id.toString())

        binding.textAuthor.setOnClickListener {
            loadProfileDetails()
        }
        binding.imageAvatar.setOnClickListener {
            loadProfileDetails()
        }
    }
    private fun loadProfileDetails(){
        val intent = Intent(this,ProfileDetailsActivity::class.java)
        intent.putExtra(AUTHORID,storyAuthor)
        startActivity(intent)
    }

    private fun initialUiTask(storyID: String){
        var ref = FirebaseDatabase.getInstance().getReference("/stories")
        val authorID = FirebaseAuth.getInstance().uid.toString()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val story=it.getValue(StoriesModel::class.java)
                    if(story!=null && story.id==storyID ){
                        binding.activitivStoryDetailsUpvoteTextView.text=(story.numberOfUpvote).toString() + " likes"
                        binding.activitivStoryDetailsCommentTextView.text=(story.numberOfComment).toString() + " comments"
                    }
                }
            }
        })

        ref = FirebaseDatabase.getInstance().getReference("/upvotes")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val upvote=it.getValue(UpvoteModel::class.java)
                    if(upvote!=null && upvote.storyID==storyID && upvote.authorID==authorID){
                        binding.activitivStoryDetailsUpvoteImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_orange_foreground))
                    }
                }
            }
        })

    }

    private fun upvotePost(storyID : String){

        val authorID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        hasUserUpvote(storyID,authorID,storyID)

    }

    private fun hasUserUpvote(curStoryID: String,curAuthorID: String,storyID: String) {
        var ref = FirebaseDatabase.getInstance().getReference("/upvotes")
        var upvoteFlag = false
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                upvoteFlag = false
                snapshot.children.forEach{
                    val upvote=it.getValue(UpvoteModel::class.java)
                    if(upvote!=null ){
                            //&& upvote.storyID==curStoryID && upvote.authorID==curAuthorID
                            val s1 = upvote.storyID
                            val s2 = upvote.authorID

                           if(s1.equals(curStoryID) && s2.equals(curAuthorID)) {
                               upvoteFlag=true
                               var ss = it.key.toString()
                               ref.child(ss).removeValue()
                               changeUpvote(-1,curStoryID)
                               Toast.makeText(this@StoryDetailsActivity,"removing like...",Toast.LENGTH_SHORT).show()
                               binding.activitivStoryDetailsUpvoteImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_gray_foreground))

                           }

                    }
                }
                if(upvoteFlag == false){
                    Log.d("upvote","upvote false")
                    val ref = FirebaseDatabase.getInstance().getReference("/upvotes").push()
                    changeUpvote(1,curStoryID)
                    binding.activitivStoryDetailsUpvoteImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_orange_foreground))
                    ref.setValue(UpvoteModel(curStoryID,curAuthorID)).addOnSuccessListener {
                        Toast.makeText(this@StoryDetailsActivity,"liking post...",Toast.LENGTH_SHORT).show()
                    }
                } else{

                }
            }
        })

    }

    private fun changeUpvote(num : Int, storyID: String){

        var ref = FirebaseDatabase.getInstance().getReference("/stories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val story=it.getValue(StoriesModel::class.java)
                    if(story!=null && story.id==storyID){
                        val numOfUpvote = story.numberOfUpvote
                        ref.child(storyID).child("numberOfUpvote").setValue((numOfUpvote.toInt()+num.toInt()))
                        binding.activitivStoryDetailsUpvoteTextView.text=(numOfUpvote.toInt()+num.toInt()).toString() + " likes"
                    }
                }
            }
        })
    }

    private fun showData(story: StoriesModel) {
        binding.textDescription.text = story.description
        binding.textDate.text = getDateString(story.timestamp)
        if(story.imageURL!="no_url"){
            Picasso.get().load(story.imageURL).into(binding.imageMain)
        }
    }

    private fun fetchStory(storyID : String){
        var ref = FirebaseDatabase.getInstance().getReference("/stories")
        var uid = ""


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val story=it.getValue(StoriesModel::class.java)
                    if(story!=null && story.id==storyID){
                        showData(story)
                        uid=story.authorID
                    }
                }
            }
        })
        ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        binding.textAuthor.text=user.name
                        storyAuthor = user
                        if(user.profilePicURL!="no_url" && user.profilePicURL!="" )
                            Glide.with(this@StoryDetailsActivity).load(user.profilePicURL).circleCrop()
                                .into(binding.imageAvatar)
                    }
                }
            }
        })
    }
    fun getDateString(time: Long) : String = simpleDateFormat.format(time * 1000L)

}


