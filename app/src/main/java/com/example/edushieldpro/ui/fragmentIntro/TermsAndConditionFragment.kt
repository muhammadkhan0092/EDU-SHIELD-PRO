package com.example.edushieldpro.ui.fragmentIntro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.edushieldpro.databinding.FragmentTermsAndConditionsBinding
import com.example.edushieldpro.ui.activities.MainActivity


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
        onRadioButtonClicked()
        onProceedClickedListener()
    }

    private fun onProceedClickedListener() {
        binding.button4.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("type","student")
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