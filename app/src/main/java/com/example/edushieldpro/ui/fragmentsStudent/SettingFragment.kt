package com.example.edushieldpro.ui.fragmentsStudent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentSettingBinding
import com.example.edushieldpro.models.User
import com.example.edushieldpro.ui.activities.IntroActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.Constants.typeStudent
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.students.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment(){
    private lateinit var binding: FragmentSettingBinding
    private lateinit var user: User
    private val viewModel by viewModels<SettingViewModel>()
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
        getUser()
        onClickListeners()
        setupBackPressListener()
        observeUserData()
    }

    private fun onClickListeners() {
        onProfileClickListener()
        onHelpClickListener()
        onLogoutClickListener()
        observeLogout()
    }

    private fun observeLogout() {
        lifecycleScope.launch {
            viewModel.logout.collectLatest {
                when(it){
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {
                        binding.progressBar10.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar10.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Logout Successfull", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.putExtra("type", typeStudent)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onLogoutClickListener() {
        binding.ivPayment.setOnClickListener {
            viewModel.logout()
        }
        binding.tvPayment.setOnClickListener {
            viewModel.logout()
        }
        binding.ivForwardPayment.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun onHelpClickListener() {
        binding.ivHelp.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_fragmentSupport)
        }
        binding.tvHelp.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_fragmentSupport)
        }
        binding.ivForwardHelp.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_fragmentSupport)
        }
    }

    private fun observeUserData() {
        lifecycleScope.launch {
            viewModel.getUser.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar10.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.progressBar10.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        user = it.data!!
                        setupData(user)
                        binding.progressBar10.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun getUser() {
        val type = "student"
        viewModel.getUser(type)
    }

    private fun setupData(user: User) {
        if(!user.image.isNullOrBlank()){
            Glide.with(requireContext()).load(user.image).into(binding.imageEdit)
        }
        binding.textView19.text = user.name
        binding.textView20.text = user.email
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
        val bundle = Bundle().also {
            it.putParcelable("user",user)
        }
        findNavController().navigate(R.id.action_settingFragment_to_profileFragment2,bundle)
    }
}