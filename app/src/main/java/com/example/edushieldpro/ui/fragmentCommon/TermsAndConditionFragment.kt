package com.example.edushieldpro.ui.fragmentCommon

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.edushieldpro.databinding.FragmentTermsAndConditionsBinding
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.ui.activities.MainActivity
import com.example.edushieldpro.utils.Constants.typeStudent
import com.google.android.play.core.integrity.client.R
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionFragment : Fragment(){
    private lateinit var binding: FragmentTermsAndConditionsBinding
    private var count = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).binding.bnbStudent.visibility = View.GONE
        (activity as HomeActivity).binding.bnbInstructor.visibility = View.GONE
        getData()
        onClickListeners()
    }

    private fun onClickListeners() {
        onRadioButtonClicked()
        onProceedClickedListener()
        onBackClickListener()
    }

    private fun onBackClickListener() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(FirebaseAuth.getInstance().currentUser==null){
                findNavController().navigate(com.example.edushieldpro.R.id.action_termsAndConditionFragment_to_welcomeFragment)
            }
            else
            {
                findNavController().navigate(com.example.edushieldpro.R.id.action_termsAndConditionFragment2_to_settingFragment)
            }
        }
    }

    private fun getData() {
        if(FirebaseAuth.getInstance().currentUser!=null){
            binding.radioButton23.visibility = View.INVISIBLE
            binding.button4.visibility = View.INVISIBLE
        }
        else
        {
            binding.radioButton23.visibility = View.VISIBLE
            binding.button4.visibility = View.VISIBLE
        }
    }

    private fun onProceedClickedListener() {
        binding.button4.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("type",typeStudent)
            startActivity(intent)
        }

    }

    private fun onRadioButtonClicked() {
        binding.radioButton23.setOnClickListener {
            if (binding.radioButton23.isChecked){
                if(count==0){
                    binding.button4.visibility = View.VISIBLE
                    count++
                }
                else
                {
                    binding.radioButton23.isChecked = false
                    binding.button4.visibility = View.INVISIBLE
                    count = 0
                }

            }
            else
            {
                binding.button4.visibility = View.INVISIBLE
            }
        }
    }
}