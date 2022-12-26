package com.example.campusappdemo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusappdemo.databinding.ActivityStoryCardBinding
import com.example.campusappdemo.databinding.StoryItemBinding
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.text.SimpleDateFormat
import java.util.*
import android.text.format.DateUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.sql.Timestamp
import java.util.concurrent.TimeUnit


class StoryCardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryCardBinding

    companion object{
        val KEY = "STORY_CARD_ACTIVITY_KEY"

        fun getDateString(timestamp : Long) : String{
            val format = SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
            var past = format.parse("2016.02.05 AD at 23:59:30");
            val now = Date();
            val stamp = Timestamp(timestamp*1000)
            past = Date(stamp.getTime())
            val seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            val minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            val hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            val days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            val months=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime())/30 +1;
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mManager = LinearLayoutManager(this)
        binding.activityStoryRecView.layoutManager=mManager

        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        fetchStories()
    }

    private fun fetchStories(){
        val ref = FirebaseDatabase.getInstance().getReference("/stories")

        ref.orderByChild("timestamp").limitToFirst(10000).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d("dberror","error")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context,StoryDetailsActivity::class.java)
                    val storyItem = item as StoryItem
                    intent.putExtra(KEY,storyItem.post)
                    startActivity(intent)
                }
                snapshot.children.forEach{
                    val post=it.getValue(StoriesModel::class.java)
                    if(post!=null){
                        adapter.add(StoryItem(post,this@StoryCardActivity))
                    }
                }
                var size = adapter.getItemCount()
                binding.activityStoryRecView.adapter=adapter
            }
        })
    }

    private fun showErrorSnackbar() {
        Snackbar.make(binding.activityStoryCardRootView,
            "Error during loading blog articles", Snackbar.LENGTH_INDEFINITE).run {
            setActionTextColor(resources.getColor(R.color.orange500))
            setAction("Retry") {
                fetchStories()
                dismiss()
            }
        }.show()
    }

}
class StoryItem(val post: StoriesModel,val context: Context): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.activityStoryItemDescTextView).text=post.description

        viewHolder.itemView.findViewById<TextView>(R.id.storyItemDateTextView).text=StoryCardActivity.getDateString(post.timestamp)
        if(post.imageURL!="no_url"){
            val myIMG = viewHolder.itemView.findViewById<ImageView>(R.id.storyItemImageMain)
            //we dont want to show image in storycard
           // Picasso.get().load(post.imageURL).into(myIMG)
        }
        getName(post.authorID,viewHolder)
        if (viewHolder.itemView.findViewById<TextView>(R.id.activityStoryItemDescTextView).text.length > 150) {
            viewHolder.itemView.findViewById<TextView>(R.id.activityStoryItemDescTextView).text = Html.fromHtml(viewHolder.itemView.findViewById<TextView>(R.id.activityStoryItemDescTextView).text.substring(0, 150) + "..." + "<font color='#F9AA33'> <u>Read More</u></font>")
        }
    }




    override fun getLayout(): Int {
        return R.layout.story_item
    }
    private fun getName(uid : String,viewHolder: ViewHolder){
        var ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val imgAvatar = viewHolder.itemView.findViewById<ImageView>(R.id.storyItemImageView)
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        viewHolder.itemView.findViewById<TextView>(R.id.storyItemNameTextView).text=user.name
                        if(user.profilePicURL!="no_url" && user.profilePicURL!="" )Glide.with(context).load(user.profilePicURL).circleCrop()
                            .into(imgAvatar)
                    }
                }
            }
        })
    }
}
