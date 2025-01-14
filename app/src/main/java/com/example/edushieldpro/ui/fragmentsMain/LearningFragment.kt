package com.example.edushieldpro.ui.fragmentsMain

import android.content.Context
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
import com.example.edushieldpro.databinding.FragmentLearningBinding
import com.example.edushieldpro.databinding.RvMyLearningItemBinding
import com.example.edushieldpro.adapters.MyLearningAdapter
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.utils.VerticalItemDecoration
import kotlin.math.abs


class LearningFragment : Fragment(){
    private lateinit var binding: FragmentLearningBinding
    private lateinit var myLearningAdapter: MyLearningAdapter
    private lateinit var backPressCallback: OnBackPressedCallback
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
        setupBackPressListener()
        setupAllCoursesRv()
        setupData()


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
        myLearningAdapter.differ.submitList(list)
    }

    private fun setupBackPressListener() {
        binding.imageView11.setOnClickListener {
            findNavController().popBackStack()
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun setupAllCoursesRv() {
        myLearningAdapter = MyLearningAdapter()
        binding.rvAllCourses.adapter = myLearningAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(40))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
    }


}