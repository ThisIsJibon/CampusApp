package com.example.campusappdemo

import android.R.attr
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.widget.EditText
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityPostCardBinding
import com.example.campusappdemo.databinding.ActivityRegistrationBinding
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.sql.Timestamp
import androidx.core.app.ActivityCompat.startActivityForResult
import android.app.Activity
import android.content.Context
import java.io.InputStream
import android.R.attr.data
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.BitmapFactory
import android.media.ExifInterface
import java.io.File
import java.io.IOException
import android.R.attr.data
import android.database.Cursor
import android.text.Html
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.net.URI
import java.util.*


class PostCardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPostCardBinding
    private var PICK_PHOTO_FOR_BLOG = 420
    private lateinit var context : Context
    private var selectedPhotoURI : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityPostCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        binding.createPostGalleryImageView.setOnClickListener{
            photoSelector()
        }
        context = this

    }
    private fun photoSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_BLOG)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_BLOG && resultCode == RESULT_OK && data!=null) {

            selectedPhotoURI = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.craetepostMainImageView.setImageDrawable(bitmapDrawable)
        }
        AlertDialog.Builder(this)
            .setTitle(Html.fromHtml("If photo gets rotated..."))
            .setMessage("try again by cropping image slightly")
            .setPositiveButton("Got It!") { dialog, which -> dialog.dismiss() }
            .show()

    }
    fun onCreatePostCancelImageViewClicked(view : View){
        finish()
    }
    fun craetepostMainImageViewClicked(view : View){
        binding.craetepostMainImageView.setImageDrawable(null)
    }
    fun onCreatePostPublishTextViewClicked(view : View){

        binding.activityPostCardProgressbar.visibility = View.VISIBLE
        binding.createPostEditText.isEnabled=false
        binding.craetepostMainImageView.isEnabled=false
        binding.createPostPublishButton.isEnabled = false

        Toast.makeText(this,"publishing post...",Toast.LENGTH_SHORT).show()
        uploadPostToFirebaseDatabase()

    }
    fun wait4Sec(){

        Handler().postDelayed({
            val intent=Intent(this,StoryCardActivity::class.java)
            startActivity(intent)
            finish()
        },4000)

    }
    private fun uploadPostToFirebaseDatabase(){


        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/stories").push()

        var desc=binding.createPostEditText.text.toString()
        val title = "blank title"

        var blogImageUrl = "no_url"
        val filename = UUID.randomUUID().toString()
        val imgref = FirebaseStorage.getInstance().getReference("/blog_images/$filename")
        if(selectedPhotoURI!=null)
            imgref.putFile(selectedPhotoURI!!).addOnSuccessListener {
            imgref.downloadUrl.addOnSuccessListener {
                blogImageUrl = it.toString()
                ref.setValue(StoriesModel(uid.toString(),ref.key.toString(),title,0,0,desc,System.currentTimeMillis()/1000,blogImageUrl)).addOnSuccessListener {
                    val intent=Intent(this,StoryCardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        else ref.setValue(StoriesModel(uid.toString(),ref.key.toString(),title,0,0,desc,System.currentTimeMillis()/1000,"no_url")).addOnSuccessListener {
            val intent=Intent(this,StoryCardActivity::class.java)
            startActivity(intent)
            finish()
        }
        //updateFirebaseData()
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
                        ref.child(uid).child("storiesPosted").setValue(user.storiesPosted+1)
                    }
                }
            }
        })


    }
}
