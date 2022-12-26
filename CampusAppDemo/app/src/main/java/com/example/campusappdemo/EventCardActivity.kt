package com.example.campusappdemo

import android.content.Intent
import android.media.metrics.Event
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusappdemo.databinding.ActivityEventCardBinding
import com.example.campusappdemo.databinding.ActivityLoginBinding
import com.example.campusappdemo.fragment.DashboardFragment
import com.example.campusappdemo.model.EventsModel
import com.example.campusappdemo.model.StoriesModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class EventCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventCardBinding

    companion object {
        val KEY = "EVENT_CARD_ACTIVITY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.eventCardRecView.layoutManager = mManager

        fetchEvents()

        binding.createEventButton.setOnClickListener {
            val intent= Intent(this,CreateEventActivity::class.java)
            finish()
            startActivity(intent)
        }


    }

    private fun fetchEvents() {
        val ref = FirebaseDatabase.getInstance().getReference("/events")
        val adapter = GroupAdapter<ViewHolder>()

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d("dberror","error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context,EventsDetailsActivity::class.java)
                    val storyItem = item as EventItem
                    intent.putExtra(EventCardActivity.KEY,storyItem.post)
                    startActivity(intent)
                }
                snapshot.children.forEach{
                    val post=it.getValue(EventsModel::class.java)
                    if(post!=null){
                        adapter.add(EventItem(post))
                    }
                }
                var size = adapter.getItemCount()
                binding.eventCardRecView.adapter=adapter
            }
        })
    }
}

class EventItem(val post: EventsModel) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.eventItemEventNameTextView).text=post.eventTitle
        viewHolder.itemView.findViewById<TextView>(R.id.eventItemStartTimeTextView).text=post.startDate + " at "+post.startTime
        viewHolder.itemView.findViewById<TextView>(R.id.eventItemEventLocationTextView).text=post.location

        if(post.eventPicURL!="no_url"){
            val myIMG = viewHolder.itemView.findViewById<ImageView>(R.id.eventItemImageView)
             Picasso.get().load(post.eventPicURL).into(myIMG)
        }
    }

    override fun getLayout(): Int {
        return R.layout.event_item
    }
}