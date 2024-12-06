package com.example.edushieldpro.ui.fragmentIntro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.edushieldpro.databinding.FragmentSignUpBinding
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity


class SignUpFragment : Fragment(){
    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        onRegisterClickListener()
    }

    private fun onRegisterClickListener() {
        binding.btnRegister.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            val type = (activity as MainActivity).type
            intent.putExtra("type",type)
            startActivity(intent)
        }
    }
}