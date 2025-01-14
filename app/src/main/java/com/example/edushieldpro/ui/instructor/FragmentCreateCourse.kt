package com.example.edushieldpro.ui.instructor

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloudinary.android.MediaManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentPurchaseBinding
import com.example.edushieldpro.adapters.AllCoursesAdapter
import com.example.edushieldpro.databinding.FragmentCreateCourseBinding
import com.example.edushieldpro.databinding.FragmentInstructorCoursesBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.utils.Constants.categories
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.CreateCourseViewModel
import com.example.edushieldpro.viewModels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentCreateCourse : Fragment(){
    private lateinit var binding: FragmentCreateCourseBinding
    private lateinit var selectedCategory : String
    private val viewModel by viewModels<CreateCourseViewModel>()
    private lateinit var downloadUrl : String
    private var realPath : String? = null
    private lateinit var uri : Uri
    var title : String = ""
    var description : String = ""
    var category : String = ""
    var rating = ""
    var sold = ""
    var price = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateCourseBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // initConfig()
        setupCategorySpinner()
        onSpinnerClickListener()
        onClickListeners()
        observeUploadingImage()
        observeCreateCourse()
    }

    private fun observeCreateCourse() {
        lifecycleScope.launch {
            viewModel.createCourse.collectLatest {
                when(it){
                    is Resource.Error ->{
                        binding.progressBar2.visibility = View.INVISIBLE
                        Log.d("khan",it.message.toString())
                    }
                    is Resource.Loading -> {
                        binding.progressBar2.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar2.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Course Created Successfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_fragmentCreateCourse_to_instructorCoursesFragment)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeUploadingImage() {
        lifecycleScope.launch {
            viewModel.cloudinary.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar2.visibility = View.INVISIBLE
                        Log.d("khan","Cloudinary ${it.message}")
                    }
                    is Resource.Loading -> {
                        binding.progressBar2.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar2.visibility = View.INVISIBLE
                        downloadUrl = it.data!!
                        viewModel.createCourse(title,description,downloadUrl,category,price)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }


    private fun onClickListeners() {
        binding.btnSubmit.setOnClickListener {
            title = binding.etCourseTitle.text.toString()
            category = selectedCategory
            description = binding.etDescription.text.toString()
            rating = "0.0"
            sold = "0"
            price = binding.etCoursePrice.text.toString()
            if (viewModel.checkValidation(title, description, uri.toString(), category)) {
                if (realPath != null) {
                    viewModel.uploadToCloudinary(realPath!!, requireContext(), {
                    }, true)
                }
            }else
            {
                Toast.makeText(requireContext(), "Fill All Fields", Toast.LENGTH_SHORT).show()
            }

        }

        binding.imageView14.setOnClickListener {
            val intent  = Intent(ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickProfileImage.launch(intent)
        }
    }
    val pickProfileImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
            uri = it
            realPath = viewModel.getRealPathFromUri(uri, requireActivity())
        }
    }

//    private fun initConfig() {
//        val config = mapOf(
//            "cloud_name" to "djd7stvwg",
//            "api_key" to "138931765972126",
//            "api_secret" to "LVzZS46qrFQiVRuXsjjEEHbRptE",
//            "secure" to true
//        )
//        MediaManager.init(requireContext(),config)
//    }


    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.etCategory.adapter = adapter
    }

    private fun onSpinnerClickListener() {
        binding.etCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
                Toast.makeText(requireContext(), "Selected: $selectedCategory", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
            }
        }
    }




}