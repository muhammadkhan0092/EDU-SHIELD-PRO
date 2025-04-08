package com.example.edushieldpro.ui.fragmentsInstructor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.adapters.ChaptersAdapter
import com.example.edushieldpro.adapters.HistoryAdapter
import com.example.edushieldpro.databinding.FragmentPurchaseHistoryBinding
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.instructor.PurchaseHistoryViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPurchaseHistory : Fragment(){
    private lateinit var binding: FragmentPurchaseHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val viewModel by viewModels<PurchaseHistoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseHistoryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAllCoursesRv()
       viewModel.getHistory(FirebaseAuth.getInstance().uid.toString())
        observeGetHistory()
    }

    private fun observeGetHistory() {
        lifecycleScope.launch {
            viewModel.getHistory.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Log.d("khan","error ${it.message}")
                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        val data = it.data
                        if(data!=null){
                            historyAdapter.differ.submitList(data)
                        }
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun setupAllCoursesRv() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.adapter = historyAdapter
        binding.rvHistory.addItemDecoration(VerticalItemDecoration(30))
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.rvHistory.isNestedScrollingEnabled = false
    }


}