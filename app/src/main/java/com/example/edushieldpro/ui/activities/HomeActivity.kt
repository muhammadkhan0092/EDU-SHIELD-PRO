package com.example.edushieldpro.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var writePreferences : SharedPreferences.Editor
    private lateinit var readPreferences : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setType()

    }

    private fun setType() {
        val data = intent.getStringExtra("type")

        // Accessing shared preferences in one go
        val sharedPreferences = getSharedPreferences("mydata", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (data != null) {
            // If data is not null, save it to SharedPreferences
            editor.putString("type", data)
            editor.apply() // Apply the change asynchronously
        }

        // Now retrieve the 'type' value from SharedPreferences
        val type = sharedPreferences.getString("type", "")

        // Check the 'type' and call corresponding method
        type?.let {
            if (it == "student") {
                bnvStudent()
            } else {
                bnvInstructor()
            }
        }
    }
    private fun bnvStudent() {
        Toast.makeText(this, "in student", Toast.LENGTH_SHORT).show()

        // Hide Instructor fragment container and make Student container visible
        val fragmentInstructor = findViewById<FragmentContainerView>(R.id.fragmentContainerViewInstructor)
        val fragmentStudent = findViewById<FragmentContainerView>(R.id.fragmentContainerViewStudent)

        binding.bnbInstructor.visibility = View.INVISIBLE
        fragmentInstructor.visibility = View.INVISIBLE

        binding.bnbStudent.visibility = View.VISIBLE
        fragmentStudent.visibility = View.VISIBLE

        // Set the icon color for the Student tab
        val colorStateList = ContextCompat.getColorStateList(this, R.color.se)
        binding.bnbStudent.itemIconTintList = colorStateList

        // Setup navigation for Student tab
        val navController = findNavController(R.id.fragmentContainerViewStudent)
        binding.bnbStudent.setupWithNavController(navController)

        binding.bnbStudent.setOnItemSelectedListener { menuItem ->
            if (navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }
            true
        }
    }

    private fun bnvInstructor() {
        val fragmentInstructor = findViewById<FragmentContainerView>(R.id.fragmentContainerViewInstructor)
        val fragmentStudent = findViewById<FragmentContainerView>(R.id.fragmentContainerViewStudent)
        binding.bnbInstructor.visibility = View.VISIBLE
        fragmentInstructor.visibility = View.VISIBLE
        binding.bnbStudent.visibility = View.INVISIBLE
        fragmentStudent.visibility = View.INVISIBLE
        val colorStateList = ContextCompat.getColorStateList(this, R.color.se)
        binding.bnbInstructor.itemIconTintList = colorStateList
        val navController = findNavController(R.id.fragmentContainerViewInstructor)
        binding.bnbInstructor.setupWithNavController(navController)
        binding.bnbInstructor.setOnItemSelectedListener { menuItem ->
            if (navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }
            true
        }
    }
}