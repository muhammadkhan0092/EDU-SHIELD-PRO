package com.example.edushieldpro.ui.instructor

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.edushieldpro.databinding.DialogAddVideBinding
import com.example.edushieldpro.databinding.FragmentInstructorCourseDetailBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.VideoData
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.InstructorCourseDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class InstructorCourseDetailFragment : Fragment(){
    private var dialog: AlertDialog? = null
    private var course : Course? = null
    private var bitmap: Bitmap? =null
    private var bitmapUrl : String? = null
    private var video : VideoData? = null
    private var videoId : String? = null
    private lateinit var dialogBinding: DialogAddVideBinding
    private val navArgs by navArgs<InstructorCourseDetailFragmentArgs>()
    private val viewModel by viewModels<InstructorCourseDetailViewModel>()
    private lateinit var binding: FragmentInstructorCourseDetailBinding
    private var uri : Uri? =null
    private var title : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructorCourseDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCourse()
        setData()
        onClickListeners()
        observeVideoId()
        observeUpdateCourse()
        observeThumbnail()
    }

    private fun setData() {
        Glide.with(requireContext()).load(course?.image).into(binding.imageView13)
    }

    private fun observeUpdateCourse() {
        lifecycleScope.launch {
            viewModel.updateCourse.collectLatest {
                when(it){
                    is Resource.Error -> {
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        dialogBinding.progressBar4.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                        dialog!!.dismiss()
                        Toast.makeText(requireContext(), "Successfully uploaded lecture", Toast.LENGTH_SHORT).show()
                        Log.d("khan","ok ${it.data}")
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun getCourse() {
        course = navArgs.course
    }

    private fun observeThumbnail(){
        lifecycleScope.launch {
            viewModel.cloudinary.collectLatest {
                when(it){
                    is Resource.Error -> {
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Thumbnail Could not be uploaded", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        dialogBinding.progressBar4.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","bitmapuri  is ${it.data}")
                        bitmapUrl = it.data
                        viewModel.uploadVideoToServer(uri!!,requireContext(),requireActivity())
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeVideoId() {
        lifecycleScope.launch {
            viewModel.uploadVideos.collectLatest {
                when(it){
                    is Resource.Error -> {
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        dialogBinding.progressBar4.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Log.d("khan","videoid  is ${it.data}")
                        videoId = it.data
                        Log.d("khan","newCourse ${course}")
                        video = VideoData(videoId!!,"",bitmapUrl!!)
                        val list = course!!.videos
                        list.add(video!!)
                        course!!.videos = list
                        Log.d("khan","oldCourse ${course}")
                        dialogBinding.progressBar4.visibility = View.INVISIBLE
                        viewModel.updateCourse(course!!)
                    }
                    is Resource.Unspecified ->{

                    }
                }
            }
        }
    }

    private fun onClickListeners() {
        binding.btnAddCourse.setOnClickListener {
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        dialogBinding = DialogAddVideBinding.inflate(LayoutInflater.from(requireContext()))
        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.button2.setOnClickListener {
            title = dialogBinding.editTextText.text.toString()
            addVideo()
        }
        dialogBinding.imageView16.setOnClickListener {
            val intent  = Intent(ACTION_GET_CONTENT)
            intent.type = "video/*"
            pickVideo.launch(intent)
        }

        dialog!!.show()
    }

    private fun addVideo() {
        if(uri==null || bitmap==null){
            Toast.makeText(requireContext(), "Please Select a Video", Toast.LENGTH_SHORT).show()
        }
        else if(title==null){
            Toast.makeText(requireContext(), "Please Enter the title", Toast.LENGTH_SHORT).show()
        }
        else if(bitmap==null){
            Toast.makeText(requireContext(), "Please Wait", Toast.LENGTH_SHORT).show()
        }
        else
        {
            viewModel.uploadBitmapToCloudinary(
                bitmap!!,
                requireContext(),
                {

                },
                true
            )
        }
    }

    private fun verifyData() {
        lifecycleScope.launch {
            if (uri != null) {
                bitmap = viewModel.extractThumbnail(uri!!, requireActivity().contentResolver)
                if (bitmap != null) {
                    dialogBinding.imageView16.setImageBitmap(bitmap)
                } else {
                    Log.e("khan", "Failed to retrieve bitmap")
                }
            }
        }

    }

    val pickVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {
            uri = it
            verifyData()
            Log.d("khan","uri is ${uri}")
        }
    }


}