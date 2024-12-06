package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentPurchaseBinding
import com.example.edushieldpro.ui.adapters.AllCoursesAdapter
import com.example.edushieldpro.ui.models.Course
import com.example.edushieldpro.ui.utils.VerticalItemDecoration


class PurchaseFragment : Fragment(){
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var allCoursesAdapter: AllCoursesAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllCoursesRv()
        binding.tvAllCourses.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseFragment_to_courseDetailFragment)
        }
        val list = listOf(
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2)
        )
        allCoursesAdapter.differ.submitList(list)
    }

    private fun setupAllCoursesRv() {
        allCoursesAdapter = AllCoursesAdapter()
        binding.rvAllCourses.adapter = allCoursesAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(30))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvAllCourses.isNestedScrollingEnabled = false
    }
}