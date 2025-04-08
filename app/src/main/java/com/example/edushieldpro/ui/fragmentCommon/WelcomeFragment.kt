package com.example.edushieldpro.ui.fragmentCommon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentWelcomeBinding
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.Constants.typeTeacher
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment(){
    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTeacherClickedListener()
        onStudentClickedListener()
        checkIfAlreadyLogedIn()
    }
    private fun checkIfAlreadyLogedIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
    private fun onStudentClickedListener() {
        binding.btnGoToStudent.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_termsAndConditionFragment)
        }
    }
    private fun onTeacherClickedListener() {
        binding.btnGoToTeacher.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("type", typeTeacher)
            startActivity(intent)
        }
    }
}