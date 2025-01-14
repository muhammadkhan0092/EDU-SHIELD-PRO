package com.example.edushieldpro.ui.fragmentsMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentPurchaseBinding
import com.example.edushieldpro.adapters.AllCoursesAdapter
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.utils.VerticalItemDecoration


class PurchaseFragment : Fragment(){
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var allCoursesAdapter: AllCoursesAdapter
    private lateinit var backPressCallback: OnBackPressedCallback
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
        onClickListener()
        setupData()
        sas()


    }

    private fun sas() {
        if(1==1){

        }
    }

    private fun setupData() {
        val list = listOf(
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web"),
            Course("","","Web Dev","WEB","",3.5,56,"56","This is a web")
        )
        allCoursesAdapter.differ.submitList(list)
    }

    private fun onClickListener() {
        binding.imageView4.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseFragment_to_homeFragment)
        }
        binding.textView14.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseFragment_to_courseDetailFragment)
        }
    }

    private fun setupAllCoursesRv() {
        allCoursesAdapter = AllCoursesAdapter()
        binding.rvAllCourses.adapter = allCoursesAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(30))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvAllCourses.isNestedScrollingEnabled = false
    }
}