package com.example.campusappdemo

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.campusappdemo.databinding.ActivityCommentLogBinding
import com.example.campusappdemo.databinding.ActivityStoryDetailsBinding
import com.example.campusappdemo.model.CommentsModel
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class CommentLogActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentLogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommentLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyID = intent.getStringExtra(StoryDetailsActivity.KEY)

        binding.activityCommentLogCommentButton.setOnClickListener{
            uploadCommentToFirebase(storyID.toString())
        }
        fetchComments(storyID.toString())
    }
    private fun uploadCommentToFirebase(storyID : String){
        val comment = binding.activityCommentLogCommentEditText.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/comments").push()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        ref.setValue(CommentsModel(uid,storyID,ref.key.toString(),comment,System.currentTimeMillis()/1000)).addOnSuccessListener {

        }
        Toast.makeText(this,"Publishing commment",Toast.LENGTH_SHORT).show()
        binding.activityCommentLogCommentEditText.text.clear()
        fetchComments(storyID)
        incrementComment(storyID)
    }

    private fun incrementComment(storyID: String){
        var ref = FirebaseDatabase.getInstance().getReference("/stories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val story=it.getValue(StoriesModel::class.java)
                    if(story!=null && story.id==storyID){
                        val num = story.numberOfComment
                        ref.child(storyID).child("numberOfComment").setValue((num.toInt()+1))
                    }
                }
            }
        })
    }

    private fun fetchComments(curStory: String){
        var ref = FirebaseDatabase.getInstance().getReference("/comments")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val comment=it.getValue(CommentsModel::class.java)
                    if(comment!=null && comment.storyID==curStory){
                        adapter.add(CommentItem(comment,this@CommentLogActivity))
                    }
                }

                binding.commentLogActivityRecView.adapter=adapter
            }
        })

    }
}

class CommentItem(val comment : CommentsModel,val context: Context): Item<ViewHolder>(){

    var commentAuthor = UserModel()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.commentItemCommentTextView).text=comment.comment
        viewHolder.itemView.findViewById<TextView>(R.id.commentItemDateTextView).text=getDateString(comment.timestamp)

        getName(comment.authorID, viewHolder)

        viewHolder.itemView.findViewById<ImageView>(R.id.commentItemImageView).setOnClickListener {
            loadProfileDetails()
        }
    }
    private fun loadProfileDetails(){
        val intent = Intent(context,ProfileDetailsActivity::class.java)
        intent.putExtra(StoryDetailsActivity.AUTHORID, commentAuthor)
        context.startActivity(intent)
    }
    override fun getLayout(): Int {
        return R.layout.comment_item
    }

    private fun getName(uid : String,viewHolder: ViewHolder){
        var ref2 = FirebaseDatabase.getInstance().getReference("/user")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        viewHolder.itemView.findViewById<TextView>(R.id.commentItemNameTextView).text=user.name
                        commentAuthor = user
                        if(user.profilePicURL!="no_url" && user.profilePicURL!="" )
                            Glide.with(context).load(user.profilePicURL).circleCrop()
                                .into(viewHolder.itemView.findViewById<ImageView>(R.id.commentItemImageView))
                    }
                }
            }
        })
    }
    private fun getDateString(timestamp : Long) : String{
        val format = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
        var past = format.parse("2016.02.05 AD at 23:59:30");
        val now = Date();
        val stamp = Timestamp(timestamp*1000)
        past = java.util.Date(stamp.getTime())
        val seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        val minutes= TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        val hours= TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        val days= TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
        val months= TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime())/30 +1;
        val years = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime())/(30*12) +1
        var ret= ""
        if(seconds<60)
        {
            ret=(seconds.toString()+" seconds ago");
        }
        else if(minutes<60)
        {
            ret=(minutes.toString()+" minutes ago");
        }
        else if(hours<24)
        {
            ret=(hours.toString()+" hours ago");
        }
        else if(days<30)
        {
            ret=(days.toString()+" days ago");
        }
        else if(months<12)
        {
            ret=(months.toString()+" months ago");
        }
        else
        {
            ret = (years.toString()+" years ago");
        }
        return ret
    }

}