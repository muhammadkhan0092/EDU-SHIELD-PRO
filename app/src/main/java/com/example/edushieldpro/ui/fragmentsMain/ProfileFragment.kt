package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.edushieldpro.databinding.FragmentProfileBinding
import com.example.edushieldpro.databinding.FragmentWelcomeBinding


class ProfileFragment : Fragment(){
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }
}