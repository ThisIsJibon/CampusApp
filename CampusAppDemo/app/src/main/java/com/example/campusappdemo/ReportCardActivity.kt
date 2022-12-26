package com.example.campusappdemo

import android.R.attr
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.text.Html
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.campusappdemo.databinding.ActivityReportCardBinding
import com.example.campusappdemo.model.NoticeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.R.attr.data
import android.os.Handler
import android.widget.Toast
import com.example.campusappdemo.model.ReportModel
import com.example.campusappdemo.model.StoriesModel
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import java.util.Scanner
import com.google.firebase.storage.UploadTask

import com.google.firebase.storage.OnProgressListener

import com.google.android.gms.tasks.Task

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.StorageReference
import java.security.AccessController.getContext
import com.google.android.gms.tasks.OnFailureListener

import android.util.Log

import com.google.android.gms.tasks.OnSuccessListener





class ReportCardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReportCardBinding
    private var selectedPhotoURI : Uri? = null
    private var selectedVideoURI : Uri? = null




    companion object{
        val PICK_PHOTO_FOR_REPORT = 420
        val PICK_VIDEO_FOR_REPORT = 421
        var imageURL = ""
        var videoURL = ""
        var videofilename = ""
        var imgfilename =""
        var REPORT_ID = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.createReportCancelImageView.setOnClickListener {
            finish()
        }
        binding.createReportAddImageButton.setOnClickListener {
            photoSelector()
        }
        binding.createReportAddVideoButton.setOnClickListener {
            videoSelector()
        }

    }
    fun onCreateEventMainImageViewClicked(view: View){
        //binding.createReportMainImageView.setImageDrawable(null)
    }
    private fun photoSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_REPORT)
    }
    private fun videoSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_FOR_REPORT)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_REPORT && resultCode == RESULT_OK && data!=null) {

            selectedPhotoURI = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.createReportAddImageButton.setText("Image Attached")
        }
        if (requestCode == PICK_VIDEO_FOR_REPORT && resultCode == RESULT_OK && data!=null) {
            selectedVideoURI= data.data
            binding.createReportAddVideoButton.setText("Video Attached")
        }
    }

    fun onCreateReportPublishTextViewClicked(view : View){



        videofilename = UUID.randomUUID().toString()
        imgfilename = UUID.randomUUID().toString()

        var videoref = FirebaseStorage.getInstance().getReference("/report_videos/$videofilename")
        uploadVideo(selectedVideoURI,videoref)
        uploadImage()

        finish()
        Toast.makeText(this,"Submitting Report",Toast.LENGTH_SHORT).show()
    }
    private fun uploadImage(){

        val reportAbout = binding.createReportReportAboutTextView.text.toString()
        val reportDesc = binding.createReportEditText.text.toString()
        val timestamp = System.currentTimeMillis()/1000

        var imgRef = FirebaseStorage.getInstance().getReference("/report_images/$imgfilename")
        var ref = FirebaseDatabase.getInstance().getReference("/reports").push()
        if (selectedPhotoURI != null) {
            imgRef.putFile(selectedPhotoURI!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(
                        applicationContext,
                        "Image Uploaded Successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    val myprofileurl = taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    imgRef.getDownloadUrl()
                        .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                            val url = task.result.toString()
                            imageURL=url
                            REPORT_ID = ref.key.toString()
                            ref.setValue(ReportModel(ref.key.toString(),reportAbout,reportDesc, imageURL,
                                imgfilename, videoURL, videofilename,timestamp))
                        })
                }).addOnFailureListener(OnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Image Uploading was failed",
                        Toast.LENGTH_LONG
                    ).show()
                })
        }
    }
    private fun uploadVideo(videoUri: Uri? , videoRef: StorageReference) {
        if (videoUri != null) {
            videoRef.putFile(videoUri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    Toast.makeText(
                        applicationContext,
                        "Video Uploaded Successfully",
                        Toast.LENGTH_LONG
                    ).show()

                    val myprofileurl = taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    videoRef.getDownloadUrl()
                        .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                            val url = task.result.toString()
                            videoURL=url
                            var ref = FirebaseDatabase.getInstance().getReference("/reports")
                            ref.child(REPORT_ID).child("videoURL").setValue(videoURL)
                        })
                }).addOnFailureListener(OnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Video Uploading was failed",
                        Toast.LENGTH_LONG
                    ).show()
                })
        } else {
            Toast.makeText(this, "Nothing to upload", Toast.LENGTH_SHORT).show()
        }
    }

    fun wait20Sec(){
        Handler().postDelayed({

        },20000)
    }


}

