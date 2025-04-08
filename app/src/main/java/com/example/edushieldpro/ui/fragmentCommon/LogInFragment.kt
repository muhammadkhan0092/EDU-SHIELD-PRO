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
import com.example.edushieldpro.databinding.FragmentLogInBinding
import com.example.edushieldpro.models.User
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.common.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment(){
    private lateinit var binding: FragmentLogInBinding
    private lateinit var email : String
    private lateinit var pass : String
    private lateinit var type : String
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
        onLoginClickListener()
        observeGetUsers()
        observeLoginWithEmail()
        observeUpdateUser()
    }

    private fun observeLoginWithEmail() {
        lifecycleScope.launch {
            viewModel.login.collectLatest{
                when(it){
                    is Resource.Loading->{
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        Log.d("khan","error is " + it.message.toString())
                    }
                    is Resource.Success -> {
                        Log.d("khan","navigating")
                        binding.progressBar5.visibility = View.INVISIBLE
                        navigateToMainActivity()
                    }

                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        Log.d("khan","navigating")
        val intent = Intent(requireContext(),HomeActivity::class.java)
        intent.putExtra("type",type)
        startActivity(intent)
    }

    private fun observeGetUsers() {
        lifecycleScope.launch {
            viewModel.usersList.collectLatest{
                when(it){
                    is Resource.Loading->{
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.d("khan",it.message.toString())
                    }
                    is Resource.Success -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        if(it.data!=null){
                            authenticateType(it.data)
                        }
                        else
                        {
                            Toast.makeText(requireContext(), "Account Not Registered", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun observeUpdateUser() {
        lifecycleScope.launch {
            viewModel.updateUser.collectLatest{
                when(it){
                    is Resource.Loading->{
                        binding.progressBar5.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        Log.d("khan",it.message.toString())
                    }
                    is Resource.Success -> {
                        binding.progressBar5.visibility = View.INVISIBLE
                        if(it.data!=null){
                            Toast.makeText(requireContext(), "Account is suspended due to multiple devices login", Toast.LENGTH_SHORT).show()
                        }
                    }

                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun authenticateType(data: List<User>) {
        var count = 0
        data.forEach {
            if(it.email==email){
                Log.d("khan","done")
                count = count + 1
                verifyStatus(it)
            }
        }
        if(data==null){
            Toast.makeText(requireContext(), "some Error", Toast.LENGTH_SHORT).show()
        }
        else if(count==0){
            Toast.makeText(requireContext(), "No Account with this credentials exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyStatus(it: User) {
        Log.d("khan","starting verification")
        if(type=="student") {
            if (it.status == 0) {
                Log.d("khan", "1")
                Toast.makeText(
                    requireContext(),
                    "Account is suspended.Contact Support team",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (it.deviceId != getDeviceId(requireContext())) {
                Log.d("khan", "2")
                it.status = 0
                it.violationType = "multipledevice"
                viewModel.updateUser(it)
            } else if (it.type != type) {
                Log.d("khan", "3")
                Toast.makeText(
                    requireContext(),
                    "You are not a ${type} Login Using ${it.type} portal",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.d("khan", "4")
                viewModel.loginWithEmailAndPassword(email, pass)
            }
        }
        else
        {
            if(it.type!=type){
                Toast.makeText(
                    requireContext(),
                    "You are not a ${type} Login Using ${it.type} portal",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                viewModel.loginWithEmailAndPassword(email,pass)
            }
        }
    }

    private fun onLoginClickListener() {
        binding.btnLogin.setOnClickListener {
            email = binding.etEmail.text.toString()
            pass = binding.etPass.text.toString()
            type = (activity as MainActivity).type
            if(viewModel.checkValidation(email,pass)){
                viewModel.getUsers(type)
            }
            else
            {
                Toast.makeText(requireContext(), "Wrong format of email or password", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}