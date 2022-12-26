package com.example.campusappdemo

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.campusappdemo.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val currentUser = auth.currentUser

        if(currentUser != null){
            if(intent.getStringExtra(RegistrationActivity.KEY)!="REG"){
                startMainActivity()
                finish()
                return
            }
            intent.putExtra(RegistrationActivity.KEY,"F")
        }

        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                onLoginClicked();
            }
        })

        binding.registerTextView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent=Intent(this@LoginActivity,RegistrationActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        binding.emailInputLayout.editText
            ?.addTextChangedListener(createTextWatcher(binding.emailInputLayout))
        binding.passInputLayout.editText
            ?.addTextChangedListener(createTextWatcher(binding.passInputLayout))

    }

    fun createTextWatcher(textInputLayout: TextInputLayout): TextWatcher{
        var textWatcher = object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                textInputLayout.isErrorEnabled=false
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        }
        return textWatcher
    }

    private fun onLoginClicked() {
        val email=binding.emailEditText.text.toString()
        val password=binding.passEditText.text.toString()
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email can not be empty"
        } else if (password.isEmpty()) {
            binding.passInputLayout.error = "Password can not be empty"
        } else{
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        performLogin(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        performLogin(null)
                    }
                }
        }
    }
    fun performLogin(user: FirebaseUser?){
        if(user!=null){
            Handler().postDelayed({
                startMainActivity()
                finish()
            }, 2000)

            binding.loginButton.visibility=View.INVISIBLE
            binding.progressBar.visibility=View.VISIBLE
            binding.passInputLayout.isEnabled=false
            binding.emailInputLayout.isEnabled=false
            binding.registerTextView.visibility=View.INVISIBLE

        } else{

        }


    }
    fun startMainActivity(){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
    private fun showErrorDialog() {

        AlertDialog.Builder(this)
            .setTitle(Html.fromHtml("<font color='#34495e'>Login Failed</font>"))
            .setMessage("Email or password is not correct.\nPlease try again or recover password.")
            .setPositiveButton("Try Again") { dialog, which -> dialog.dismiss() }
            .setNegativeButton(Html.fromHtml("<font color='#e74c3c'>Forgot Password</font>")){dialog,which->dialog.dismiss(); forgetPass()}
            .show()

    }
    fun forgetPass(){
        Toast.makeText(this,"recovering pass",Toast.LENGTH_SHORT).show()
    }

}