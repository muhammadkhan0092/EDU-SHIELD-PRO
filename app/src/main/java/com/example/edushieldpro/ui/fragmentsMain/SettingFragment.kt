package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentSettingBinding


class SettingFragment : Fragment(){
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onProfileClickListener()
        setupBackPressListener()
    }

    private fun setupBackPressListener() {
        binding.imageView12.setOnClickListener {
            findNavController().popBackStack()
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
           findNavController().popBackStack()
        }
    }

    private fun onProfileClickListener() {
        binding.ivProfile.setOnClickListener {
            navigateToProfile()
        }
        binding.ivForwardProfile.setOnClickListener {
            navigateToProfile()
        }
        binding.tvProfile.setOnClickListener {
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_settingFragment_to_profileFragment2)
    }
}