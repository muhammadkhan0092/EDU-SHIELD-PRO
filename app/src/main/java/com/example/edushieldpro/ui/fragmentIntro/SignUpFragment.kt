package com.example.edushieldpro.ui.fragmentIntro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.edushieldpro.databinding.FragmentSignUpBinding
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.RegisterValidation
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignUpFragment : Fragment(){
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<RegisterViewModel>()
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
        observeRegistration()
        observeEtext()
    }

    private fun observeEtext() {
        lifecycleScope.launch {
            viewModel.validation.collect{fieldState->
                if (fieldState.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etEmail.apply {
                            requestFocus()
                            error = fieldState.email.message
                        }
                    }
                }
                if(fieldState.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etPassword.apply {
                            requestFocus()
                            error = fieldState.password.message
                        }
                    }
                }
            }
        }
    }

    private fun observeRegistration() {
        lifecycleScope.launch {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading->{
                        binding.btnRegister.visibility = View.INVISIBLE
                    }
                    is Resource.Error -> {
                        Log.d("khan",it.message.toString())
                    }
                    is Resource.Success -> {
                        val activity = (activity as MainActivity)
                        Log.d("khan","done")
                        val intent = Intent(requireContext(),HomeActivity::class.java)
                        intent.putExtra("type",activity.type)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun onRegisterClickListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPassword.text.toString()
            val rePass = binding.etPassword.text.toString()
            val cnic = binding.etCnic.text.toString()
            val phone = binding.etPhoneNo.text.toString()
            val type = (activity as MainActivity).type
           viewModel.createAccountWithEmailAndPassword(name,email,pass,cnic,phone,rePass,type)
        }
    }
}