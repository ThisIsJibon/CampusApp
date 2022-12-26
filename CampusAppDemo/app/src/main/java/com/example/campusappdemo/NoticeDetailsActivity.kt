package com.example.campusappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.example.campusappdemo.databinding.ActivityNoticeDetailsBinding
import com.example.campusappdemo.model.EventsModel
import com.example.campusappdemo.model.NoticeModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class NoticeDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNoticeDetailsBinding
    var curAuthor = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.noticeDetailsAttachmentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        val notice = intent.getParcelableExtra<NoticeModel>(NoticeCardActivity.KEY)
        initVar(notice!!)

    }
    private fun initVar(notice : NoticeModel){

        binding.noticeDetailsNoticeTitle.text=notice.noticeTitle
        binding.noticeDetailsNoticeDesc.text = notice.noticeDesc
        val s1 = "<a href=\""
        val s2 = notice.attachmentLink
        val s3 = "\">Visit Link</a>"
        val link =s1+s2+s3
        binding.noticeDetailsAttachmentTextView.text =  Html.fromHtml(link)
        binding.noticeDetailsDeadlineTextView.text = notice.deadline
        binding.noticeDetailsPostedDateTextview.text = StoryCardActivity.getDateString(notice.timestamp)
        getName(notice.authorID)

        if(notice.deadline==""){
            binding.noticeDetailsDeadlineLiteral.visibility= View.GONE
            binding.noticeDetailsDeadlineTextView.visibility= View.GONE
        }

        if(notice.attachmentLink==""){
            binding.noticeDetailsAttachmentLiteral.visibility= View.GONE
            binding.noticeDetailsAttachmentTextView.visibility= View.GONE
        }
        binding.noticeDetailsPostedByHostNameTextView.setOnClickListener {
            loadProfileDetails()
        }

    }

    private fun loadProfileDetails(){
        val intent = Intent(this,ProfileDetailsActivity::class.java)
        intent.putExtra(StoryDetailsActivity.AUTHORID, curAuthor)
        startActivity(intent)
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
                        binding.noticeDetailsPostedByHostNameTextView.text=user.name
                        curAuthor = user
                    }
                }
            }
        })
    }
}