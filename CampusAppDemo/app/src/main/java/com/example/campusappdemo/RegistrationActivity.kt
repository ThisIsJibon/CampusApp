package com.example.campusappdemo

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.campusappdemo.databinding.ActivityRegistrationBinding
import com.example.campusappdemo.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import android.R

import androidx.annotation.NonNull




class RegistrationActivity : AppCompatActivity() {
    companion object{
        val KEY="REGISTRATION_ACTIVITY"
    }

    private lateinit var binding : ActivityRegistrationBinding
    lateinit var email : String
    lateinit var password : String
    lateinit var repeatPass : String
    lateinit var name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVar()

    }

    override fun onBackPressed() {
        val intent =  Intent(this@RegistrationActivity,LoginActivity::class.java);
        finish()
        startActivity(intent);
        finishActivity(0);
    }
    fun wait1Sec(){

        Handler().postDelayed({


            val intent=Intent(this@RegistrationActivity,LoginActivity::class.java)
            intent.putExtra(RegistrationActivity.KEY,"REG")
            startActivity(intent)
            finish()
        },2000)

        binding.registerButton.visibility=View.INVISIBLE
        binding.regProgressBar.visibility=View.VISIBLE
        binding.nameEditText.isEnabled=false
        binding.regPassEditText.isEnabled=false
        binding.regRepeatPassEditText.isEnabled=false
        binding.regEmailEditText.isEnabled=false
    }


    fun registerButtonAction(view: View) {
        
        initVar()
        val ERROR_TYPE =checkRegistrationCases()

        if(ERROR_TYPE!=5){
            Toast.makeText(this,"Error while register.\nError Type $ERROR_TYPE",Toast.LENGTH_SHORT).show()
        } else{
            registerFirebaseUser(ERROR_TYPE)
        }
    }
    fun initVar(){
        email = binding.regEmailEditText.text.toString()
        password = binding.regPassEditText.text.toString()
        repeatPass=binding.regRepeatPassEditText.text.toString()
        name=binding.nameEditText.text.toString()
    }

    fun registerFirebaseUser(ERROR_TYPE: Int){
        val auth= Firebase.auth

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("AUTH", "createUserWithEmail:success ${task.result?.user?.uid}")
                    val user = auth.currentUser
                    loadUI(user)
                    saveUserToFirebaseDatabase()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    loadUI(null)
                }
            }
    }

    private fun saveUserToFirebaseDatabase(){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.setValue(UserModel(name,email,uid,0,0,0,0,0,0,"")).addOnSuccessListener {
            Log.d("auth","success uid")
        }

    }
    fun loadUI(user : FirebaseUser?){

        if(user==null){
            //checkRegistrationCases()
            showErrorDialog(5)
        }
        else wait1Sec()
    }
    fun checkRegistrationCases() : Int{
        if(password.length<6){
            showErrorDialog(1)
            return 1
        } else if(password!=repeatPass){
            showErrorDialog(2)
            return 2
        } else if(name.isEmpty()){
            showErrorDialog(3)
            return 3
        } else if(email.isEmpty()){
            showErrorDialog(4)
            return 4
        }
        return 5
    }
    private fun showErrorDialog(ERROR_TYPE : Int) {
        var errorString =""
        if(ERROR_TYPE==1){
            errorString="Password must contain 6 characters or more."
            binding.regPassEditText.text?.clear()
            binding.regRepeatPassEditText.text?.clear()
        } else if(ERROR_TYPE==2){
            errorString="Password and Repeat Password entry are not same."
            binding.regPassEditText.text?.clear()
            binding.regRepeatPassEditText.text?.clear()
        } else if(ERROR_TYPE==3){
            errorString="Enter a name."
        } else if(ERROR_TYPE==4){
            errorString="Enter Email."
        } else if(ERROR_TYPE==5){
            errorString="This Email is already registered!"
        }
        AlertDialog.Builder(this)
            .setTitle(Html.fromHtml("<font color='#34495e'>Registration Failed</font>"))
            .setMessage("$errorString \nPlease try again")
            .setPositiveButton("Try Again") { dialog, which -> dialog.dismiss() }
            .show()

    }

}
