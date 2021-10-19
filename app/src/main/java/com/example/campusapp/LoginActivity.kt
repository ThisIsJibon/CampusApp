package com.example.campusapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import android.text.TextWatcher as TextWatcher

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        addWatcher()
        checkLoginState()
    }

    private fun checkLoginState() {
        var sharedPreferences=getSharedPreferences("BOOLEAN_KEY", Context.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        var isLoggedIn=sharedPreferences.getBoolean("BOOLEAN_KEY",false)
        if(isLoggedIn){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return;
        }
        editor.apply {
            putBoolean("BOOLEAN_KEY",true)
        }.apply()
    }

    fun addWatcher(){
        textPasswordInput.editText?.addTextChangedListener(myWatcherMethod(textPasswordInput))
        textUsernameLayout.editText?.addTextChangedListener(myWatcherMethod(textUsernameLayout))
    }
    fun myWatcherMethod(textInputLayout: TextInputLayout): TextWatcher{
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



    fun loginProgress(){
        loginProgressBar.visibility=View.VISIBLE
        loginButton.visibility = View.INVISIBLE
        textUsernameLayout.isEnabled = false
        textPasswordInput.isEnabled=false
        goToMainActivity()
    }
    fun goToMainActivity(){
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
    fun loginValidation(){

        var username= textUsernameLayout.getEditText()?.getText().toString()
        var password=textPasswordInput.getEditText()?.getText().toString()

        if(username.isEmpty()){
            textUsernameLayout.setError("username can't be empty")
            textUsernameLayout.setErrorEnabled(true)
        }
        if(password.isEmpty()){
            textPasswordInput.setError("password can't be empty")
            textPasswordInput.setErrorEnabled(true)
        }


        if(username.equals("a") and password.equals("a")) {
            loginProgress()
        }
        else showErrorDialog()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Failed")
            .setMessage("Password or Username is incorrect")
            .setPositiveButton("OK"){
                    dialog,which->{
                    dialog.dismiss()
                }
            }
            .show()
    }

    fun loginButtonAction(v: View){
        loginValidation()
       // loginProgress()
    }

}