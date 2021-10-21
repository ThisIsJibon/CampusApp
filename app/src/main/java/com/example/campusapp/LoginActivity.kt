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
import com.example.campusapp.databinding.ActivityLoginBinding
import com.example.campusapp.databinding.ActivityMainBinding
import com.google.android.material.textfield.TextInputLayout
import android.text.TextWatcher as TextWatcher


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater) // 1
        setContentView(binding.root)

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
        binding.textPasswordInput.editText?.addTextChangedListener(myWatcherMethod(binding.textPasswordInput))
        binding.textUsernameLayout.editText?.addTextChangedListener(myWatcherMethod(binding.textUsernameLayout))
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
        binding.loginProgressBar.visibility=View.VISIBLE
        binding.loginButton.visibility = View.INVISIBLE
        binding.textUsernameLayout.isEnabled = false
        binding.textPasswordInput.isEnabled=false
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

        var username= binding.textUsernameLayout.getEditText()?.getText().toString()
        var password=binding.textPasswordInput.getEditText()?.getText().toString()

        if(username.isEmpty()){
            binding.textUsernameLayout.setError("username can't be empty")
            binding.textUsernameLayout.setErrorEnabled(true)
        }
        if(password.isEmpty()){
            binding.textPasswordInput.setError("password can't be empty")
            binding.textPasswordInput.setErrorEnabled(true)
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
    }

}