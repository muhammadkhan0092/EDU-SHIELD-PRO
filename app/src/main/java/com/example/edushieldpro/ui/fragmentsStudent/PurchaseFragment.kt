package com.example.edushieldpro.ui.fragmentsStudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.students.PurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseFragment : Fragment(){
    private lateinit var binding: FragmentPurchaseBinding
    private lateinit var allCoursesAdapter: AllCoursesAdapter
    private lateinit var backPressCallback: OnBackPressedCallback
    private val viewModel by viewModels<PurchaseViewModel>()
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
        observeGetCourses()
    }

    private fun observeGetCourses() {
        lifecycleScope.launch {
            viewModel.getCourses.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar6.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar6.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val data = it.data
                        if(data!=null){
                            allCoursesAdapter.differ.submitList(data)
                        }
                        binding.progressBar6.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }





    private fun onClickListener() {
        onBackPressed()
        binding.imageView4.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseFragment_to_homeFragment)
        }
        binding.textView14.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseFragment_to_courseDetailFragment)
        }

        allCoursesAdapter.onClick = {c->
            val bundle = Bundle().also {
                it.putParcelable("Course",c)
                it.putString("Status","0")
                it.putString("from","purchase")
            }
            findNavController().navigate(R.id.action_purchaseFragment_to_courseDetailFragment,bundle)
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_purchaseFragment_to_homeFragment)
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