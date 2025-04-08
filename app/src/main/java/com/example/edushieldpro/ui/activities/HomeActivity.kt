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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    lateinit var SS : BottomNavigationView
    private lateinit var writePreferences : SharedPreferences.Editor
    private lateinit var readPreferences : SharedPreferences
    lateinit var t : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        SS = binding.bnbStudent
        setContentView(binding.root)
        setType()
    }


    private fun setType() {
        val data = intent.getStringExtra("type")
        Log.d("khan","${data}")
        val sharedPreferences = getSharedPreferences("mydata", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (data != null) {
            if (data == "student") {
                Log.d("khan","displaying student")
                bnvStudent()
            } else {
                Log.d("khan","displaying teacher")
                bnvInstructor()
            }
            t = data
            editor.putString("type", data)
            editor.apply()
        }
        else
        {
            val type = sharedPreferences.getString("type", "")
            Log.d("khan","type is ${type}")
            type?.let {
                t = it
                if (it == "student") {
                    Log.d("khan","displaying student")
                    bnvStudent()
                } else {
                    Log.d("khan","displaying teacher")
                    bnvInstructor()
                }
            }
        }

    }
    private fun bnvStudent() {
        Toast.makeText(this, "in student", Toast.LENGTH_SHORT).show()
        val fragmentInstructor = findViewById<FragmentContainerView>(R.id.fragmentContainerViewInstructor)
        val fragmentStudent = findViewById<FragmentContainerView>(R.id.fragmentContainerViewStudent)

        binding.bnbInstructor.visibility = View.INVISIBLE
        fragmentInstructor.visibility = View.INVISIBLE

        binding.bnbStudent.visibility = View.VISIBLE
        fragmentStudent.visibility = View.VISIBLE
        val colorStateList = ContextCompat.getColorStateList(this, R.color.se)
        binding.bnbStudent.itemIconTintList = colorStateList

        val navHostFragment= supportFragmentManager.findFragmentById(R.id.fragmentContainerViewStudent) as NavHostFragment
        val navController= navHostFragment.navController
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
            Log.d("hehe","changing")
            if (navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }
            true
        }
    }

    fun handleVisibility(){
        val navController = findNavController(R.id.fragmentContainerViewInstructor)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("hamza","${destination.id}")
            if(destination.id == R.id.videoFragment) {

                binding.bnbStudent.visibility = View.GONE
            } else {

                binding.bnbStudent.visibility = View.VISIBLE
            }
        }
    }
}