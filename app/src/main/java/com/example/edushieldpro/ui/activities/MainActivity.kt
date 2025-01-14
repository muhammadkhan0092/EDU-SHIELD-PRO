package com.example.edushieldpro.ui.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var currentSection = "login"
    var type = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("type").toString()
        setupUi()
        onLoginClickListener()
        onSignUpClickListener()
        var a : String? = intent.getStringExtra("category")
        if (a != null) {
            binding.tvDesc.text = a
        }
    }



    private fun setupUi() {
        setTitle(type,"login")
        setDescription(type,"login")
    }


    private fun onLoginClickListener() {
        binding.btnLog.setOnClickListener {
            if(currentSection!="login"){
                it.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.black)
                )
                binding.btnSign.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.green)
                )
                currentSection = "login"
                setDescription(type,"login")
                setTitle(type,"login")
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_signUpFragment_to_logInFragment)
            }
        }
    }

    private fun onSignUpClickListener() {
        binding.btnSign.setOnClickListener {
            if(currentSection!="sign"){
                currentSection = "sign"
                it.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.black)
                )
                binding.btnLog.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.green)
                )
                setTitle(type,"sign")
                setDescription(type,"sign")
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_logInFragment_to_signUpFragment)
            }
        }
    }
    private fun setTitle(type : String,page : String) {
        if(type == "teacher"){
            if(page == "login"){
                binding.tvHeading.text = "Welcome Back InstrnLog in to continue where you left off"
            }
            else if(page=="sign")
            {
                binding.tvHeading.text = "Dear Instructor!\nGo Ahead and set up your account"
            }
        }
        else if(type=="student")
        {
            if(page == "login"){
                binding.tvHeading.text = "Welcome Back Student!\nLog in to continue where you left off"
            }
            else if(page=="sign")
            {
                binding.tvHeading.text = "Dear Learner!\nGo Ahead and set up your account"
            }
        }
    }

    private fun setDescription(type : String,page : String) {
        if(type == "teacher"){
            if(page == "login"){
                binding.tvDesc.text = "Login to continue educating"
            }
            else if(page=="sign")
            {
                binding.tvDesc.text = "Sign up and Start educating"
            }
        }
        else if(type=="student")
        {
            if(page == "login"){
                binding.tvDesc.text = "Log in -> one step closer to education"
            }
            else if(page=="sign")
            {
                binding.tvDesc.text = "Sign up to enjoy Quality courses"
            }
        }
    }
}