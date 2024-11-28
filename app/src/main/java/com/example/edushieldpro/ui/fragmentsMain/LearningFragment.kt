package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.edushieldpro.databinding.FragmentLearningBinding
import com.example.edushieldpro.databinding.FragmentWelcomeBinding


class LearningFragment : Fragment(){
    private lateinit var binding: FragmentLearningBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningBinding.inflate(inflater,container,false)
        return binding.root
    }
}