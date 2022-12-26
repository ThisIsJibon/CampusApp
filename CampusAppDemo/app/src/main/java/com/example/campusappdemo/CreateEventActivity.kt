package com.example.campusappdemo

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.widget.DatePicker
import java.util.*
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Html

import android.widget.TimePicker
import android.widget.Toast
import com.example.campusappdemo.databinding.ActivityCreateEventBinding
import com.example.campusappdemo.databinding.ActivityEventCardBinding
import com.example.campusappdemo.model.EventsModel
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


class CreateEventActivity : AppCompatActivity() {
    private var datePickerDialog: DatePickerDialog? = null
    private lateinit var binding: ActivityCreateEventBinding
    var hour = 0
    var minute = 0
    private var PICK_PHOTO_FOR_EVENT = 420
    private lateinit var context : Context
    private var selectedPhotoURI : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDatePicker();
        binding.createEventGalleryImageView.setOnClickListener{
            photoSelector()
        }
        binding.createEventDatePickerButton.setText(getTodaysDate());
    }

    private fun photoSelector(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_PHOTO_FOR_EVENT)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PHOTO_FOR_EVENT && resultCode == RESULT_OK && data!=null) {

            selectedPhotoURI = data.data

            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            binding.createEventMainImageView.setImageDrawable(bitmapDrawable)
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(Html.fromHtml("If photo gets rotated..."))
            .setMessage("try again by cropping image slightly")
            .setPositiveButton("Got It!") { dialog, which -> dialog.dismiss() }
            .show()

    }
    
    fun onCreateEventCancelImageViewClicked(view : View){
        startActivity(Intent(this,EventCardActivity::class.java))
        finish()
    }
    
    fun onCreateEventMainImageViewClicked(view : View){
        binding.createEventMainImageView.setImageDrawable(null)
    }

    fun onCreateEventHostButtonClicked(view: View){
        Toast.makeText(this,"publishing event...", Toast.LENGTH_SHORT).show()
        finish()
        uploadEventToFirebaseDatabase()
        startActivity(Intent(this,EventCardActivity::class.java))
    }
    
    private fun uploadEventToFirebaseDatabase(){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/events").push()

        var desc = binding.createEventDescEditText.text.toString()
        val title = binding.createEventTitleEditText.text.toString()
        val startDate = binding.createEventDatePickerButton.text.toString()
        val startTime = binding.createEventTimeButton.text.toString()
        val going = 0
        val location = binding.createEventLocationEditText.text.toString()
        val eventID = ref.key.toString()
        val duration = binding.createEventDurationEditText.text.toString()
        var eventImageUrl = "no_url"


        val filename = UUID.randomUUID().toString()
        val imgref = FirebaseStorage.getInstance().getReference("/event_images/$filename")

        if(selectedPhotoURI!=null)
            imgref.putFile(selectedPhotoURI!!).addOnSuccessListener {
                imgref.downloadUrl.addOnSuccessListener {
                    eventImageUrl = it.toString()
                    ref.setValue(EventsModel(eventID,title,desc,uid,duration,startTime,startDate,going,location,eventImageUrl)).addOnSuccessListener {
                        val intent=Intent(this,CreateEventActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        else ref.setValue(EventsModel(eventID,title,desc,uid,duration,startTime,startDate,going,location,"no_url")).addOnSuccessListener {
            finish()
        }
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
                        ref.child(uid).child("eventsPosted").setValue(user.eventsPosted+1)
                    }
                }
            }
        })


    }
    
    private fun getTodaysDate(): String? {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        var month: Int = cal.get(Calendar.MONTH)
        month = month + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month = month + 1
                val date = makeDateString(day, month, year)
                binding.createEventDatePickerButton.setText(date)
            }

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH]
        val day = cal[Calendar.DAY_OF_MONTH]

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String? {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        if (month == 1) return "JAN"
        if (month == 2) return "FEB"
        if (month == 3) return "MAR"
        if (month == 4) return "APR"
        if (month == 5) return "MAY"
        if (month == 6) return "JUN"
        if (month == 7) return "JUL"
        if (month == 8) return "AUG"
        if (month == 9) return "SEP"
        if (month == 10) return "OCT"
        if (month == 11) return "NOV"
        return if (month == 12) "DEC" else "JAN"

        //default should never happen
    }

    fun openDatePicker(view: View?) {
        datePickerDialog?.show()
    }
    fun popTimePicker(view: View?) {
        val onTimeSetListener =
            OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                binding.createEventTimeButton.setText(
                    java.lang.String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour,
                        minute
                    )
                )
            }

        // int style = AlertDialog.THEME_HOLO_DARK;
        val timePickerDialog =
            TimePickerDialog(this,  /*style,*/onTimeSetListener, hour, minute, true)
        timePickerDialog.setTitle("Select Time")
        timePickerDialog.show()
    }
}