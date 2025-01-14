package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentCourseDetailBinding
import com.example.edushieldpro.adapters.AllCoursesAdapter
import com.example.edushieldpro.adapters.ChaptersAdapter
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.utils.VerticalItemDecoration


class CourseDetailFragment : Fragment(){
    private lateinit var chaptersAdapter: ChaptersAdapter
    private lateinit var binding: FragmentCourseDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAllCoursesRv()
    }

    private fun setupAllCoursesRv() {
        chaptersAdapter = ChaptersAdapter()
        binding.rvAllCourses.adapter = chaptersAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(30))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
         binding.rvAllCourses.isNestedScrollingEnabled = false
    }
}