package com.example.edushieldpro.ui.fragmentsStudent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.adapters.MyLearningAdapter
import com.example.edushieldpro.databinding.FragmentLearningBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.ProgressData
import com.example.edushieldpro.models.SingleProgress
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.students.LearningViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LearningFragment : Fragment(){
    private var job : Job? =null
    private lateinit var binding: FragmentLearningBinding
    private lateinit var myLearningAdapter: MyLearningAdapter
    private lateinit var course: MutableList<Course>
    private lateinit var progressData : ProgressData
    private val viewModel by viewModels<LearningViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearningBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllCoursesRv()
        setupBackPressListener()
        viewModel.getCourses(FirebaseAuth.getInstance().uid!!)
        observeGetCourses()
        observeProgress()
        onTextChanged()
        addKeyboardDetectListener()
    }

    private fun onTextChanged() {
        binding.editTextText2.addTextChangedListener { editable->
            job?.cancel()
            job = MainScope().launch {
                delay(600L)
                binding.progressBar7.visibility = View.VISIBLE
                val query = binding.editTextText2.text.toString()
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        val filteredCourses = course.filter { it.title.contains(query, ignoreCase = true) }
                        val f2 = course.filter { it.category.contains(query, ignoreCase = true) }
                        val c = filteredCourses.union(f2)
                        myLearningAdapter.differ.submitList(c.toList())
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                    else
                    {
                        myLearningAdapter.differ.submitList(course)
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private fun addKeyboardDetectListener() {
        val window = (activity as? HomeActivity)?.window ?: return
        val topView = window.decorView.findViewById<View>(android.R.id.content)

        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val heightDifference = topView.rootView.height - topView.height
            if (heightDifference > dpToPx(requireContext(), 200F)) {
                Log.d("khan", "keyboard shown")
                (activity as HomeActivity).binding.bnbStudent.visibility = View.GONE
            } else {
                Log.d("khan", "keyboard hidden")
                CoroutineScope(Dispatchers.Main).launch{
                    delay(100L)
                    (activity as HomeActivity).binding.bnbStudent.visibility = View.VISIBLE
                }

            }
        }

        topView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener!!)
    }

    private fun observeProgress() {
        lifecycleScope.launch {
            viewModel.completed.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar7.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar7.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        progressData = it.data!!
                        Log.d("hassan","data received is ${progressData}")
                        updateCourseDataModel(progressData)
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun updateCourseDataModel(progressData: ProgressData) {
        course.forEach {cour->
            cour.videos.forEach {vid->
                progressData.singleProgress.forEach { pro ->
                    if (vid.videoId == pro.videoId) {
                        vid.watchedData = pro.timeData
                    }
                }
            }
        }
        myLearningAdapter.differ.submitList(course)
    }

    private fun observeGetCourses() {
        lifecycleScope.launch {
            viewModel.getCourses.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar7.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        course = it.data!!
                        val progressData  = ProgressData(FirebaseAuth.getInstance().uid.toString(),
                            mutableListOf()
                        )
                        course.forEach {cour->
                            cour.videos.forEach {v->
                                val singleProgress = SingleProgress(v.videoId, TimeData(0,0))
                                progressData.singleProgress.add(singleProgress)
                            }
                        }
                        viewModel.progress(progressData)
                        binding.progressBar7.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }


    private fun setupBackPressListener() {
        binding.imageView11.setOnClickListener {
            findNavController().navigate(R.id.action_learningFragment_to_purchaseFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_learningFragment_to_purchaseFragment)
        }
        myLearningAdapter.onClick = {c->
            val bundle = Bundle().also {
                it.putParcelable("Course",c)
                it.putString("Status","1")
                it.putString("from","learning")
            }
            findNavController().navigate(R.id.action_learningFragment_to_courseDetailFragment,bundle)
        }
    }

    private fun setupAllCoursesRv() {
        myLearningAdapter = MyLearningAdapter()
        binding.rvAllCourses.adapter = myLearningAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(40))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
    }
    fun dpToPx(context: Context, valueInDp: Float) : Float{
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        removeKeyboardDetectListener()
    }
    private fun removeKeyboardDetectListener() {
        val window = (activity as? HomeActivity)?.window ?: return
        val topView = window.decorView.findViewById<View>(android.R.id.content)

        globalLayoutListener?.let {
            topView.viewTreeObserver.removeOnGlobalLayoutListener(it)
            globalLayoutListener = null
        }
    }



}