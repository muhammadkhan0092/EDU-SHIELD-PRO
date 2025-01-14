package com.example.edushieldpro.ui.instructor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentPurchaseBinding
import com.example.edushieldpro.adapters.AllCoursesAdapter
import com.example.edushieldpro.adapters.InstructorCoursesAdapter
import com.example.edushieldpro.databinding.FragmentInstructorCoursesBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.CreateCourseViewModel
import com.example.edushieldpro.viewModels.InstructorCoursesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstructorCoursesFragment : Fragment(){
    private val viewModel by viewModels<InstructorCoursesViewModel>()
    private lateinit var binding: FragmentInstructorCoursesBinding
    private lateinit var instructorCoursesAdapter: InstructorCoursesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructorCoursesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRv()
        observeGetCourses()
        onClickListeners()
    }

    private fun setupRv() {
        instructorCoursesAdapter = InstructorCoursesAdapter()
        binding.insCoursesRv.adapter = instructorCoursesAdapter
        binding.insCoursesRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.insCoursesRv.addItemDecoration(VerticalItemDecoration(50))
    }

    private fun onClickListeners() {
        binding.btnAddCourse.setOnClickListener {
            findNavController().navigate(R.id.action_instructorCoursesFragment_to_fragmentCreateCourse)
        }
        instructorCoursesAdapter.onClick = {course->
            val bundle = Bundle().also {
                it.putParcelable("course",course)
            }
            findNavController().navigate(R.id.action_instructorCoursesFragment_to_instructorCourseDetailFragment,bundle)
        }
    }

    private fun observeGetCourses() {
        lifecycleScope.launch {
            viewModel.getCourses.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Log.d("khan","the Error is ${it.message}")
                        binding.progressBar3.visibility = View.INVISIBLE
                    }
                    is Resource.Loading ->{
                        binding.progressBar3.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar3.visibility = View.INVISIBLE
                        instructorCoursesAdapter.differ.submitList(it.data)
                        Log.d("khan","the data is ${it.data}")
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }


}