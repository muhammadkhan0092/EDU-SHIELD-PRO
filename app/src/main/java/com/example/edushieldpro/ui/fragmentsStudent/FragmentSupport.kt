package com.example.edushieldpro.ui.fragmentsStudent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.edushieldpro.adapters.SupportAdapter
import com.example.edushieldpro.databinding.FragmentSupportBinding
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.students.SupportViewModel
import com.example.edushieldpro.models.MessageModel
import com.example.hazir.data.SingleMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

@AndroidEntryPoint
class FragmentSupport : Fragment(){
    private lateinit var binding: FragmentSupportBinding
    private val viewModel by viewModels<SupportViewModel>()
    private lateinit var messageModel: MessageModel
    private lateinit var messages : MutableList<SingleMessage>
    private lateinit var supportAdapter: SupportAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBnB()
        setupMessageAdapter()
        retreiveMessages()
        observeMessagesRetreived()
        observeMessageSent()
        onClickListeners()
    }

    private fun hideBnB() {
        (activity as HomeActivity).binding.bnbStudent.visibility = View.GONE
    }

    private fun onClickListeners() {
        binding.button6.setOnClickListener {
            val msg = binding.editTextText3.text.toString()
            messages.add(SingleMessage(getRandomId(),"user",msg))
            messageModel.messages = messages
            viewModel.addNewMessage(messageModel)
        }
    }

    private fun setupMessageAdapter() {
        supportAdapter = SupportAdapter()
        binding.rv.adapter = supportAdapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
       // binding.rv.addItemDecoration(VerticalItemDecoration(10))
    }

    private fun observeMessagesRetreived() {
        lifecycleScope.launch {
            viewModel.retreive.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar13.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar13.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        messageModel = it.data!!
                        messages = messageModel.messages.toMutableList()
                        supportAdapter.differ.submitList(messages)
                        binding.progressBar13.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeMessageSent() {
        lifecycleScope.launch {
            viewModel.add.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.progressBar13.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar13.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar13.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun retreiveMessages() {
        viewModel.retreiveMessages()
    }
    private fun getRandomId() : String{
        return UUID.randomUUID().toString()
    }


}