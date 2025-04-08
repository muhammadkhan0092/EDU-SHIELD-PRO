package com.example.edushieldpro.ui.fragmentCommon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.edushieldpro.databinding.FragmentSignUpBinding
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.RegisterValidation
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.common.RegisterViewModel
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
                        binding.progressBar14.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.progressBar14.visibility = View.INVISIBLE
                        Log.d("khan",it.message.toString())
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Success -> {
                        binding.progressBar14.visibility = View.INVISIBLE
                        val activity = (activity as MainActivity)
                        Log.d("khan","done")
                        Toast.makeText(requireContext(), "Sign Up Successfull", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireContext(), "type is ${type}", Toast.LENGTH_SHORT).show()
            val deviceId : String = getDeviceId(requireContext())
           viewModel.createAccountWithEmailAndPassword(name,email,pass,cnic,phone,rePass,type,1,deviceId)
        }
    }
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}