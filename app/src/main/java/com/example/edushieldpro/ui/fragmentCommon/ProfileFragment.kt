package com.example.edushieldpro.ui.fragmentCommon

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentProfileBinding
import com.example.edushieldpro.models.User
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.common.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private  var uri: Uri? = null
    private lateinit var type:String
    private var realPath : String? = null
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var user: User
    private val navArgs by navArgs<ProfileFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        onClickListeners()
        observeImageUpload()
        observeUpdateUser()
        observeGetUser()
    }

    private fun observeGetUser() {
        Log.d("khan","getting")
        lifecycleScope.launch {
            viewModel.getUser.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Log.d("khan","err")
                        binding.progressBar11.visibility  = View.INVISIBLE
                        Toast.makeText(requireContext(), "Error getting teacher", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar11.visibility  = View.VISIBLE
                    }
                    is Resource.Success ->{
                        Log.d("khan","sss")
                        user = it.data!!
                        setUserDetail(user)
                        binding.progressBar11.visibility  = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeImageUpload() {
        lifecycleScope.launch {
            viewModel.image.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar11.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar11.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                       Log.d("khan","image uplaoded")
                        binding.progressBar11.visibility = View.VISIBLE
                        user.image = it.data!!
                        viewModel.setUser(type,user)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }


    private fun observeUpdateUser() {
        lifecycleScope.launch {
            viewModel.update.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar11.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar11.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","user updated")
                        Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                        binding.progressBar11.visibility = View.VISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        binding.imageView12.setOnClickListener {
            if(type=="teacher"){
                findNavController().navigate(R.id.action_profileFragment2_to_instructorCoursesFragment)
            }
            else if(type=="student"){
                findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
            }
        }
        binding.button5.setOnClickListener {
            val intent  = Intent(ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickProfileImage.launch(intent)
        }
        binding.button.setOnClickListener {
            val name = binding.etName.text.toString()
            user.name = name
            if(realPath!=null){
                viewModel.uploadImage(realPath!!)
            }
            else
            {
                viewModel.setUser(type,user)
            }
        }
    }

    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        if(imageUri==null){

        }
        else
        {
            binding.button5.text = "Edit"
            uri = imageUri
            realPath = getRealPathFromUri(uri)
            Glide.with(requireContext()).load(uri).into(binding.imageEdit)
        }

    }

    private fun setData() {
        type = (activity as HomeActivity).t
        Log.d("khan","type : ${type}")
        if(type=="student"){
            user = navArgs.user
            setUserDetail(user)
        }
        else if(type=="teacher"){
            Log.d("khan","teacher")
            viewModel.getUser(type)
        }

    }

    private fun setUserDetail(user: User) {
        binding.etName.text = Editable.Factory.getInstance().newEditable(user.name)
        if(!user.image.isNullOrBlank()){
            Glide.with(requireContext()).load(user.image).into(binding.imageEdit)
        }
        else
        {
            binding.button5.text = "Add"
        }
    }

    fun getRealPathFromUri(imageUri: Uri?): String? {
        val cursor: Cursor? = requireActivity().contentResolver.query(imageUri!!, null, null, null, null)
        return if (cursor == null) {
            imageUri.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }
}