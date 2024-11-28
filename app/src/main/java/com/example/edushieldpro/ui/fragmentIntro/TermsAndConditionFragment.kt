package com.example.edushieldpro.ui.fragmentIntro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.edushieldpro.databinding.FragmentTermsAndConditionsBinding


class TermsAndConditionFragment : Fragment(){
    private lateinit var binding: FragmentTermsAndConditionsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater,container,false)
        return binding.root
    }
}