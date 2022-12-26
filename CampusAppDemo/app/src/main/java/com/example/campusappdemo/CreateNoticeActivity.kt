package com.example.campusappdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityCreateNoticeBinding
import com.example.campusappdemo.model.NoticeModel
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*

class CreateNoticeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateNoticeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.createNoticeCancelImageView.setOnClickListener {
            finish()
        }
        binding.createNoticePublishButton.setOnClickListener {
            binding.createNoticeProgressbar.visibility= View.VISIBLE
            Toast.makeText(this,"publishing notice ...",Toast.LENGTH_SHORT).show()
            uploadNoticeToFirebase()
        }
    }
    private fun uploadNoticeToFirebase() {

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/notice").push()
        val noticeid = ref.key.toString()
        val title = binding.createNoticeTitleEditText.text.toString()
        val desc = binding.createNoticeDescEditText.text.toString()
        val link = binding.createNoticeLinkeEditText.text.toString()
        val deadline = binding.createNoticeDeadlineEditText.text.toString()
        val timestamp = System.currentTimeMillis() / 1000
        ref.setValue(NoticeModel(noticeid, title, desc, uid, link, deadline, timestamp))
            .addOnSuccessListener {
                finish()
            }
        startActivity(Intent(this,NoticeCardActivity::class.java))
        updateFirebaseData()
    }
    private fun updateFirebaseData(){
        val uid = FirebaseAuth.getInstance().uid.toString()


        var ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                snapshot.children.forEach{
                    val user=it.getValue(UserModel::class.java)
                    if(user!=null && user.uid==uid){
                        ref.child(uid).child("totalPost").setValue(user.totalPost+1)
                        ref.child(uid).child("noticePosted").setValue(user.noticePosted+1)
                    }
                }
            }
        })


    }
}