package com.example.campusappdemo.fragment

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.campusappdemo.*
import com.example.campusappdemo.databinding.FragmentProfileBinding
import com.example.campusappdemo.model.StoriesModel
import com.example.campusappdemo.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*


class ProfileFragment : Fragment() {

    private var selectedPhotoURI : Uri? = null
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dialog: Dialog
    private var filePath: Uri? = null
    private lateinit var imagePicker: ImageView

    val REQUEST_CODE = 100


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val currentUser = FirebaseAuth.getInstance().currentUser




        setUserProfilePicture()

        userPosts()

        setUserInfo()

        logOut()

        UploadProfile()

        EditProfile()



        return binding.root
    }


    private fun logOut() {

        mAuth = FirebaseAuth.getInstance()

        binding.logout.setOnClickListener() {

            mAuth.signOut()


            val intent = Intent(getActivity(), LoginActivity::class.java)
            startActivity(intent)

            getActivity()?.onBackPressed();

        }

    }


    private fun setUserInfo() {

        if (activity == null)
            return


        val ref = FirebaseDatabase.getInstance().getReference("/user")
        val uid = FirebaseAuth.getInstance().uid.toString()
        if (uid.isNotEmpty()) {
            ref.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    binding.profileFragmentNameTextView.text = user?.name
                    binding.profileFragmentPostsEmailView.text = user?.email
                    if (user != null) {
                        context?.let {
                            Glide.with(it).load(user.profilePicURL).circleCrop()
                                .into(binding.profileFragmentProfileImageView)
                        }
                        binding.profileFragmentPostsTextView.text = user.totalPost.toString()
                        binding.profileFragmentFollowersTextView.text = user.followerNum.toString()
                        binding.profileFragmentFollowingTextView.text = user.followingNum.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }


    }


    private fun userPosts() {

        binding.profilePostsLinearlayout.setOnClickListener {
            Toast.makeText(it.context, "PostsLinearLayout is Clicked", Toast.LENGTH_SHORT).show()
        }

    }


    private fun setUserProfilePicture() {

        binding.profileFragmentProfileImageView.setOnClickListener {
            dialog = Dialog(it.context)
            dialog.setContentView(R.layout.popup_window_profile_image)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var gallery = dialog.findViewById<LinearLayout>(R.id.selectProfilePictureLinearLayout)



            imagePicker = binding.profileFragmentProfileImageView

            gallery.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
            }


            val cancel = dialog.findViewById<TextView>(R.id.cancelButton)
            val upload = dialog.findViewById<TextView>(R.id.uploadProfilePictureButton)



            cancel.setOnClickListener {
                cancelButton()
            }

            upload.setOnClickListener {

                UploadProfile()
                dialog.dismiss()
            }
            dialog.show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data!=null) {

            selectedPhotoURI = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,selectedPhotoURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            //binding.profileFragmentProfileImageView.setImageDrawable(bitmapDrawable)
            Glide.with(this).load(bitmap).circleCrop()
                .into(binding.profileFragmentProfileImageView)
        }

    }


    private fun UploadProfile() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val storageReferencee = FirebaseStorage.getInstance().getReference("profile/$uid.jpg")

        val filename = uid
        var dpUrl = ""
        val imgref = FirebaseStorage.getInstance().getReference("/profile/$filename")
        if(selectedPhotoURI!=null)
            imgref.putFile(selectedPhotoURI!!).addOnSuccessListener {
                imgref.downloadUrl.addOnSuccessListener {
                    dpUrl = it.toString()
                    val ref = FirebaseDatabase.getInstance().getReference("/user")
                        .child(FirebaseAuth.getInstance().uid.toString()).child("profilePicURL").setValue(dpUrl)
                }
            }

    }

    private fun cancelButton() {
        dialog.dismiss()
    }


    private fun EditProfile() {

        binding.profileButton.setOnClickListener {

            val intent = Intent(activity, EditProfileFragmentActivity::class.java)
            startActivity(intent)

        }
    }


}

