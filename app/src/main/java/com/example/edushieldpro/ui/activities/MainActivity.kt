package com.example.edushieldpro.ui.activities

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var currentSection = "login"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onLoginClickListener()
        onSignUpClickListener()

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
                findNavController(R.id.fragmentContainerView).navigate(R.id.action_logInFragment_to_signUpFragment)
            }
        }
    }
}