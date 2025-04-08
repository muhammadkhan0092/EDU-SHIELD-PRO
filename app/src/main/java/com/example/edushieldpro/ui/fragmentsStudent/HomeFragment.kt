package com.example.edushieldpro.ui.fragmentsStudent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.adapters.PopularCoursesAdapter
import com.example.edushieldpro.adapters.TopTeachersAdapter
import com.example.edushieldpro.databinding.FragmentHomeBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPopularCourses
import com.example.edushieldpro.models.DataTopTeachers
import com.example.edushieldpro.models.User
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.students.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var topTeachersAdapter: TopTeachersAdapter
    private lateinit var popularCoursesAdapter: PopularCoursesAdapter
    private lateinit var courses : List<Course>
    private lateinit var teachers:MutableList<User>
    private val viewModel by viewModels<HomeViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupTopTeachersRv()
        setupPopularCoursesRv()
        observeGetCourses()
        observeTeachers()
        onClickListeners()
    }

    private fun onClickListeners() {
        popularCoursesAdapter.onClick = {
            onPopularCourseListener(it)
        }
    }

    private fun onPopularCourseListener(dataPopularCourses: DataPopularCourses) {
        val course = courses.filter { it.courseId==dataPopularCourses.courseId }
        val bundle = Bundle().also {
            it.putParcelable("Course",course.first())
            it.putString("Status","0")
            it.putString("from","home")
        }
        findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment,bundle)
    }

    private fun observeTeachers() {
        lifecycleScope.launch {
            viewModel.getTeachers.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar12.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar12.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar12.visibility = View.INVISIBLE
                        teachers = it.data!!
                        setupTopTeachersData(teachers)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun setupTopTeachersData(teachers: MutableList<User>) {
        val dataTopTeachers : MutableList<DataTopTeachers> = mutableListOf()
        Log.d("khan","teachers ${teachers}")
        teachers.forEach {
            dataTopTeachers.add(DataTopTeachers(it.name,it.courses.toString(),it.image))
        }
        topTeachersAdapter.differ.submitList(dataTopTeachers)
    }

    private fun observeGetCourses() {
        lifecycleScope.launch {
            viewModel.getCourses.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar12.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar12.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        courses = it.data!!
                        binding.progressBar12.visibility = View.INVISIBLE
                        val data = it.data!!
                        findTopUUID(data)

                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun findTopUUID(courses: List<Course>) {
        val sortedStudents = courses.sortedByDescending { it.sold }
        val popularCourses : MutableList<DataPopularCourses> = mutableListOf()
        if(sortedStudents.size<=5){
            sortedStudents.forEach {
                popularCourses.add(DataPopularCourses(it.title,it.students.size.toString(),it.image,it.courseId))
            }
            popularCoursesAdapter.differ.submitList(popularCourses)
        }
        else
        {
            val sliced = sortedStudents.slice(1..5).toMutableList()
            sliced.forEach {
                popularCourses.add(DataPopularCourses(it.title,it.students.size.toString(),it.image,it.courseId))
            }
            popularCoursesAdapter.differ.submitList(popularCourses)
        }

    }

    private fun setupTopTeachersRv() {
        topTeachersAdapter = TopTeachersAdapter()
        binding.rvTopTeachers.adapter = topTeachersAdapter
        binding.rvTopTeachers.addItemDecoration(VerticalItemDecoration(30))
        binding.rvTopTeachers.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvTopTeachers.isNestedScrollingEnabled = false
    }

    private fun setupPopularCoursesRv() {
        popularCoursesAdapter = PopularCoursesAdapter()
        binding.rvCourses.adapter = popularCoursesAdapter
        binding.rvCourses.addItemDecoration(VerticalItemDecoration(30))
        binding.rvCourses.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.rvTopTeachers.isNestedScrollingEnabled = false
    }
}