package com.example.campusappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusappdemo.databinding.ActivityNoticeCardBinding
import com.example.campusappdemo.model.NoticeModel
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import java.util.*

class NoticeCardActivity : AppCompatActivity() {

    override fun onBackPressed() {
        finish()
    }

    private lateinit var binding: ActivityNoticeCardBinding

    companion object {
        val KEY = "NOTICE_CARD_ACTIVITY_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoticeCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mManager = LinearLayoutManager(this)
        binding.noticeCardNoticeRecView.layoutManager = mManager

        binding.noticeCardCreateNoticeButton.setOnClickListener {
            finish()
            startActivity(Intent(this, CreateNoticeActivity::class.java))
        }

        fetchNotice()
    }

    private fun fetchNotice() {
        val ref = FirebaseDatabase.getInstance().getReference("/notice")

        ref.orderByChild("timestamp").limitToFirst(10000).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, NoticeDetailsActivity::class.java)
                    val noticeItem = item as NoticeItem
                    intent.putExtra(NoticeCardActivity.KEY, noticeItem.post)
                    startActivity(intent)
                }
                snapshot.children.forEach {
                    val post = it.getValue(NoticeModel::class.java)
                    if (post != null) {
                        adapter.add(NoticeItem(post))
                    }
                }
                binding.noticeCardProgressBar.visibility = View.GONE
                var size = adapter.getItemCount()
                binding.noticeCardNoticeRecView.adapter = adapter
            }
        })
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            binding.noticeCardRootView,
            "Error during loading notices. Check Internet connection.", Snackbar.LENGTH_INDEFINITE
        ).run {
            setActionTextColor(resources.getColor(R.color.orange500))
            setAction("Retry") {
                fetchNotice()
                dismiss()
            }
        }.show()
    }

}

class NoticeItem(val post: NoticeModel) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.findViewById<TextView>(R.id.noticeItemNoticeDesc).text = post.noticeDesc
        viewHolder.itemView.findViewById<TextView>(R.id.noticeItemNoticeTitle).text =
            post.noticeTitle
        viewHolder.itemView.findViewById<TextView>(R.id.noticeItemPostedDateTextview).text =
            "Posted: " + StoryCardActivity.getDateString(post.timestamp)
        getName(post.authorID.toString(), viewHolder)
        val deadlineTV = viewHolder.itemView.findViewById<TextView>(R.id.noticeItemDeadlineTextview)
        deadlineTV.text = post.deadline
        if (viewHolder.itemView.findViewById<TextView>(R.id.noticeItemNoticeDesc).text.length > 150) {
            viewHolder.itemView.findViewById<TextView>(R.id.noticeItemNoticeDesc).text =
                Html.fromHtml(
                    viewHolder.itemView.findViewById<TextView>(R.id.noticeItemNoticeDesc).text.substring(
                        0,
                        150
                    ) + "..." + "<font color='#F9AA33'> <u>Read More</u></font>"
                )
        }
        if (deadlineTV.text == "") {
            deadlineTV.visibility = View.GONE
            viewHolder.itemView.findViewById<TextView>(R.id.noticeItemDeadlineLiteral).visibility =
                View.GONE
        }

    }

    override fun getLayout(): Int {
        return R.layout.notice_item
    }

    private fun getName(uid: String, viewHolder: ViewHolder) {
        var ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(UserModel::class.java)
                    if (user != null && user.uid == uid) {
                        viewHolder.itemView.findViewById<TextView>(R.id.noticeItemPostedByHostNameTextView).text =
                            user.name
                    }
                }
            }
        })
    }

}
