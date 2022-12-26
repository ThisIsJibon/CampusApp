package com.example.campusappdemo

import android.app.usage.UsageEvents
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityEventCardBinding
import com.example.campusappdemo.databinding.ActivityEventsDetailsBinding
import com.example.campusappdemo.model.EventsModel
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UpvoteModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class EventsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventsDetailsBinding
    private lateinit var curEventID : String
    var curAuthorID =""
    var curAuthor = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val event = intent.getParcelableExtra<EventsModel>(EventCardActivity.KEY)
        initVar(event!!)
        binding.eventDetailsDescTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    private fun initVar(event : EventsModel){

        isGoingtoEvent()
        binding.eventDetailsPeopleGoingTextView.text=event.going.toString() + " going"
        curEventID = event.eventID
        curAuthorID = FirebaseAuth.getInstance().uid.toString()
        Picasso.get().load(event.eventPicURL).into(binding.eventDetailsEventImageView)
        if(event.eventPicURL=="no_url") binding.eventDetailsEventImageView.setImageDrawable(resources.getDrawable(R.drawable.sydney_image))
        binding.eventDetailsEventNameTextView.text = event.eventTitle
        binding.eventDetailsDescTextView.text = Html.fromHtml(event.eventDesc)
        binding.eventDetailsStartTimeTextView.text = event.startDate + " at "+event.startTime
        binding.eventDetailsEventLocationTextView.text = event.location
        getName(event.authorID)
        binding.eventDetailsDurationTextView.text = event.duration

        binding.eventDetailsHostTextView.setOnClickListener {
            loadProfileDetails()
        }

    }
    private fun loadProfileDetails(){
        val intent = Intent(this,ProfileDetailsActivity::class.java)
        intent.putExtra(StoryDetailsActivity.AUTHORID, curAuthor)
        startActivity(intent)
    }

    private fun isGoingtoEvent(){
        var ref = FirebaseDatabase.getInstance().getReference("/event_going")
        var goingFlag = false
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val event_going_item=it.getValue(EventGoingModel::class.java)
                    if(event_going_item!=null ){
                        val s1 = event_going_item.eventID
                        val s2 = event_going_item.userID
                        if(s1.equals(curEventID) && s2.equals(curAuthorID)) {
                            binding.eventDetailsRemindMeImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_orange_foreground))
                        }

                    }
                }
            }
        })
    }
    private fun getName(uid : String){
        var ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        binding.eventDetailsHostTextView.text = user.name
                        curAuthor = user
                    }
                }
            }
        })
    }

    fun onEventDetailsRemindMeImageViewClick(view : View){
        var ref = FirebaseDatabase.getInstance().getReference("/event_going")
        var goingFlag = false
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                goingFlag = false
                snapshot.children.forEach{
                    val event_going_item=it.getValue(EventGoingModel::class.java)
                    if(event_going_item!=null ){
                        //&& upvote.storyID==curStoryID && upvote.authorID==curAuthorID
                        val s1 = event_going_item.eventID
                        val s2 = event_going_item.userID
                        if(s1.equals(curEventID) && s2.equals(curAuthorID)) {
                            goingFlag=true
                            var ss = it.key.toString()
                            ref.child(ss).removeValue()
                            changeNumberOfGoing(-1)
                            Toast.makeText(this@EventsDetailsActivity,"removing going status...",Toast.LENGTH_SHORT).show()
                            binding.eventDetailsRemindMeImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_gray_foreground))
                        }

                    }
                }
                if(goingFlag == false){
                    Log.d("upvote","upvote false")
                    val ref = FirebaseDatabase.getInstance().getReference("/event_going").push()
                    binding.eventDetailsRemindMeImageView.setImageDrawable(getDrawable(R.drawable.ic_upvote_orange_foreground))
                    changeNumberOfGoing(1)
                    ref.setValue(EventGoingModel(curEventID,curAuthorID)).addOnSuccessListener {
                        Toast.makeText(this@EventsDetailsActivity,"going to event...",Toast.LENGTH_SHORT).show()
                    }
                } else{

                }
            }
        })
    }
    private fun changeNumberOfGoing(num: Int){
        var ref = FirebaseDatabase.getInstance().getReference("/events")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val event=it.getValue(EventsModel::class.java)
                    if(event!=null && event.eventID==curEventID){
                        ref.child(curEventID).child("going").setValue((event.going.toInt()+num.toInt()))
                    }
                }
            }
        })
    }
}


data class EventGoingModel(val eventID: String, val userID: String) {
    constructor():this("","")
}