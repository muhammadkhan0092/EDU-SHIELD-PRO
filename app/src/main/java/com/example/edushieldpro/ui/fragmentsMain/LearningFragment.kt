package com.example.edushieldpro.ui.fragmentsMain

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.R
import com.example.edushieldpro.databinding.FragmentLearningBinding
import com.example.edushieldpro.databinding.RvMyLearningItemBinding
import com.example.edushieldpro.ui.adapters.MyLearningAdapter
import com.example.edushieldpro.ui.models.Course
import com.example.edushieldpro.ui.utils.VerticalItemDecoration
import kotlin.math.abs


class LearningFragment : Fragment(){
    private lateinit var binding: FragmentLearningBinding
    private lateinit var myLearningAdapter: MyLearningAdapter
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
        val view = RvMyLearningItemBinding.inflate(LayoutInflater.from(requireContext()))
        view.flexibleView.post {
            Log.d("khan","ok : ${view.flexibleView.width}")
        }
        setupAllCoursesRv()

        val l = listOf(
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,1),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,2),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,3),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,4),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,5),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,6),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,7),
            Course("helllo","Graphic Designing","Graphic Designing Advanced", R.drawable.google,4.1,8)
        )
        myLearningAdapter.differ.submitList(l)
    }

    private fun setupAllCoursesRv() {
        myLearningAdapter = MyLearningAdapter()
        binding.rvAllCourses.adapter = myLearningAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(40))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
    }

    fun getViewSizeInDP(view: View, context: Context): Pair<Float, Float> {
        val widthInPixels = view.width.toFloat()
        val heightInPixels = view.height.toFloat()
        val density = context.resources.displayMetrics.density
        val widthInDP = widthInPixels / density
        val heightInDP = heightInPixels / density

        return Pair(widthInDP, heightInDP)
    }
}