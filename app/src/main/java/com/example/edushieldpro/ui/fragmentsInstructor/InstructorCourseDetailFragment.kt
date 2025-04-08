package com.example.edushieldpro.ui.fragmentsInstructor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.edushieldpro.adapters.VideosAdapter
import com.example.edushieldpro.databinding.DialogAddVideBinding
import com.example.edushieldpro.databinding.FragmentInstructorCourseDetailBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.models.VideoData
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.instructor.InstructorCourseDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class InstructorCourseDetailFragment : Fragment(){
    private var dialog: AlertDialog? = null
    private var whichUpdate : String = ""
    private lateinit var videoData: VideoData
    private var course : Course? = null
    private lateinit var videos : List<VideoData>
    private var bitmap: Bitmap? =null
    private var bitmapUrl : String? = null
    private var video : VideoData? = null
    private var videoId : String? = null
    private lateinit var videosAdapter: VideosAdapter
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
        setupVideoAdapter()
        getCourse()
        setData()
        onClickListeners()
        observeVideoId()
        observeUpdateCourse()
        observeThumbnail()
    }

    private fun setupVideoAdapter() {
        videosAdapter = VideosAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = videosAdapter
        binding.recyclerView.addItemDecoration(VerticalItemDecoration(30))
    }

    private fun setData() {
        if(course?.videos?.size==0){
            binding.recyclerView.visibility = View.INVISIBLE
        }
        else
        {
            videos = course?.videos!!
            binding.textView22.visibility = View.INVISIBLE
            videosAdapter.differ.submitList(videos)
        }
        Glide.with(requireContext()).load(course?.image).into(binding.imageView13)
        binding.textView14.text = course?.title
    }

    private fun getVideoDurationFormatted(context: Context, videoUri: Uri): TimeData {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, videoUri)
            val durationMillis = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

            val minutes = (durationMillis / 1000) / 60
            val seconds = (durationMillis / 1000) % 60
            return TimeData(minutes.toInt(),seconds.toInt())
        } catch (e: Exception) {
            e.printStackTrace()
            TimeData(0,0)
        } finally {
            retriever.release()
        }
    }


    private fun observeUpdateCourse() {
        lifecycleScope.launch {
            viewModel.updateCourse.collectLatest {
                when(it){
                    is Resource.Error -> {
                        if(whichUpdate=="dialog"){
                            dialogBinding.progressBar4.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        else if(whichUpdate=="adapter"){
                            binding.progressBar3.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                        whichUpdate = ""
                    }
                    is Resource.Loading -> {
                        if(whichUpdate=="dialog"){
                            dialogBinding.progressBar4.visibility = View.VISIBLE
                        }
                        else if(whichUpdate=="adapter"){
                            binding.progressBar3.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Success -> {
                        if(whichUpdate=="dialog"){
                            addVideoToRv()
                            dialogBinding.progressBar4.visibility = View.INVISIBLE
                            dialog!!.dismiss()
                            Toast.makeText(requireContext(), "Successfully uploaded lecture", Toast.LENGTH_SHORT).show()
                        }
                        else if(whichUpdate=="adapter"){
                            binding.progressBar3.visibility = View.INVISIBLE
                            removeVideosFromRv()
                            Toast.makeText(requireContext(), "Successfully deleted lecture", Toast.LENGTH_SHORT).show()
                        }

                        whichUpdate = ""
                    }
                    is Resource.Unspecified -> {

                    }

                }
            }
        }
    }

    private fun removeVideosFromRv() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.textView22.visibility = View.INVISIBLE
        val currentImages = videosAdapter.differ.currentList.toMutableList()
        Log.d("KHAN","ABHI $currentImages")
        Log.d("KHAN","ABHI $video")
        currentImages.remove(video)
        Log.d("KHAN","NEW LIST $currentImages")
        videosAdapter.differ.submitList(currentImages)
        binding.progressBar3.visibility = View.INVISIBLE
    }

    private fun addVideoToRv() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.textView22.visibility = View.INVISIBLE
        val currentImages = videosAdapter.differ.currentList.toMutableList()
        currentImages.add(video)
        videosAdapter.differ.submitList(currentImages)
    }

    private fun getCourse() {
        course = navArgs.course
        Log.d("khan","courses is $course")
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
                        title?.let { it1 ->
                            viewModel.uploadVideoToServer(uri!!,requireContext(),requireActivity(),
                                it1
                            )
                        }
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
                        videoId = it.data
                        val time = getVideoDurationFormatted(requireContext(),uri!!)
                        Log.d("khan","this is duration $time")
                        video = title?.let { it1 -> VideoData(videoId!!,"",bitmapUrl!!, it1,time) }
                        val list = course!!.videos
                        list.add(video!!)
                        course!!.videos = list
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

        videosAdapter.onDel = { _, v->
            videoData = v
            video = v
            whichUpdate = "adapter"
            course?.videos?.remove(video)
            viewModel.updateCourse(course!!)
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
            whichUpdate = "dialog"
            viewModel.uploadBitmapToCloudinary(
                bitmap!!
            ) {

            }
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

    private val pickVideo = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val intent = it.data
        val imageUri = intent?.data
        imageUri?.let {ur->
            uri = ur
            verifyData()
            Log.d("khan","uri is $uri")
        }
    }


}