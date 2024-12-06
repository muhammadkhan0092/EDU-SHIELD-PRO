package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentCourseDetailBinding
import com.example.edushieldpro.databinding.FragmentProfileBinding
import com.example.edushieldpro.ui.adapters.AllCoursesAdapter
import com.example.edushieldpro.ui.adapters.ChaptersAdapter
import com.example.edushieldpro.ui.models.Course
import com.example.edushieldpro.ui.utils.VerticalItemDecoration


class ProfileFragment : Fragment() {
    private lateinit var chaptersAdapter: ChaptersAdapter
    private lateinit var binding: FragmentProfileBinding
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
    }
}