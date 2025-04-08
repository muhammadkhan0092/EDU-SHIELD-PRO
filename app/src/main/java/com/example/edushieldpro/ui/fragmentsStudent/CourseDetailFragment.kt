package com.example.edushieldpro.ui.fragmentsStudent

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.edushieldpro.R
import com.example.edushieldpro.adapters.ChaptersAdapter
import com.example.edushieldpro.databinding.FragmentCourseDetailBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPurchaseHistory
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.VerticalItemDecoration
import com.example.edushieldpro.viewModels.students.CourseDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@AndroidEntryPoint
class CourseDetailFragment : Fragment(){
    private lateinit var chaptersAdapter: ChaptersAdapter
    private lateinit var course: Course
    private lateinit var from : String
    private lateinit var paymentSheet: PaymentSheet
    private var clientSecret : String? = null
    private lateinit var binding: FragmentCourseDetailBinding
    private val viewModel by viewModels<CourseDetailViewModel>()
    private val navArgs by navArgs<CourseDetailFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).binding.bnbStudent.visibility = View.GONE
        setupPaymentConfiguration()
        setupAllCoursesRv()
        receiveNavData()
        onClicKListeners()
        observeUpdateStudent()
        observeClientSecretKey()
    }

    private fun observeClientSecretKey() {
        lifecycleScope.launch {
            viewModel.payment.collectLatest {
                when(it){
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                    is Resource.Success ->{
                        val secret = it.data
                        if (secret != null) {
                            Log.d("khan","secret key is ${secret}")
                            clientSecret = secret
                            paymentSheet.presentWithPaymentIntent(clientSecret!!, PaymentSheet.Configuration("EDU SIELD PRO"))
                        } else {
                            Toast.makeText(requireContext(), "Error creating payment", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPaymentConfiguration() {
        PaymentConfiguration.init(requireContext(), "pk_test_51QxTWtIkUIJaPemPJSiBIfdLnm0kZj78rXEwoUHepaTVWBfz9jRHNDw4TVVZIbuK2SQrNtW0RL7TnvV7iRxGtlh900vWlg3CY0") // 🔥 Use your Stripe publishable key
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(requireContext(), "Payment Successful!", Toast.LENGTH_LONG).show()
                updateCourseAndHistory()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(requireContext(), "Payment Failed: ${paymentSheetResult.error.message}", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(requireContext(), "Payment Canceled", Toast.LENGTH_LONG).show()
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCourseAndHistory() {
        course.students.add(FirebaseAuth.getInstance().uid.toString())
        course.sold = course.sold + 1
        val current = LocalDateTime.now()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = current.format(dateFormatter)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val currentTime = current.format(timeFormatter)
        val email = FirebaseAuth.getInstance().currentUser?.email
        val purchaseData = DataPurchaseHistory(getRandomId(),"",email!!,course.title,course.image,currentDate,currentTime)
        viewModel.updateCourseAndSetHistory(course,purchaseData)
    }

    private fun startPayment() {
        viewModel.payment(amount = 1000)
    }

    private fun onBackClicked() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as HomeActivity).binding.bnbStudent.visibility = View.VISIBLE
            if(from=="video" || from=="learning"){
                findNavController().navigate(R.id.action_courseDetailFragment_to_learningFragment)
            }
            else if(from=="home")
            {
                findNavController().navigate(R.id.action_courseDetailFragment_to_homeFragment)
            }
            else
            {
                findNavController().navigate(R.id.action_courseDetailFragment_to_purchaseFragment)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeUpdateStudent() {
        lifecycleScope.launch {
            viewModel.update.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error Occured ${it.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar9.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar9.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Course Purcahsed Successfully", Toast.LENGTH_SHORT).show()
                        (activity as HomeActivity).binding.bnbStudent.visibility = View.VISIBLE
                        findNavController().navigate(R.id.action_courseDetailFragment_to_learningFragment)
                        binding.progressBar9.visibility = View.INVISIBLE
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }



    fun getRandomId() : String{
        return UUID.randomUUID().toString()
    }


    private fun onClicKListeners() {
        onCourseCLicked()
        onBackClicked()
        onButtonClicked()
    }

    private fun onButtonClicked() {
        binding.button3.setOnClickListener {
            startPayment()
        }
    }


    private fun onCourseCLicked() {
        chaptersAdapter.onClick = { vid ->
            if (from == "video" || from == "learning") {
                val bundle = Bundle().also {
                    it.putParcelable("video", vid)
                    it.putParcelable("course", course)
                }
                findNavController().navigate(
                    R.id.action_courseDetailFragment_to_videoFragment,
                    bundle
                )
            } else {

            }
        }
    }


    private fun receiveNavData() {
        setData()
        if(from=="video"){
            setupFromVideo()
        }
        else if(from=="learning")
        {
            setupFromLearning()
        }
        else if(from=="home"){
            setupFromHome()
        }
        else {
            setupFromPurchase()
        }
    }

    private fun setupFromHome() {
        chaptersAdapter.type = 2
        chaptersAdapter.differ.submitList(course.videos)
    }

    private fun setupFromPurchase() {
        chaptersAdapter.type = 2
        chaptersAdapter.differ.submitList(course.videos)
    }

    private fun setupFromLearning() {
        binding.button3.visibility = View.GONE
        chaptersAdapter.type = 1
        chaptersAdapter.differ.submitList(course.videos)
    }

    private fun setupFromVideo() {
        binding.button3.visibility = View.INVISIBLE
        chaptersAdapter.type = 0
        binding.progressBar9.visibility = View.INVISIBLE
        chaptersAdapter.differ.submitList(course.videos)
    }

    private fun setData() {
        course = navArgs.Course
        from = navArgs.from
        binding.textView12.text = course.title
        Glide.with(requireContext()).load(course.image).into(binding.imageView9)
        binding.tvTotalLessons.text =  course.videos.size.toString() + "Lessons"
        var minutes = 0
        var second = 0
        course.videos.forEach {
            minutes = it.timeData.minutes + minutes
            second = it.timeData.seconds + second
        }
        binding.tvTotalTime.text = "$minutes MINS $second SECS"
        binding.rvDescription.text = course.description
    }

    private fun setupAllCoursesRv() {
        chaptersAdapter = ChaptersAdapter()
        binding.rvAllCourses.adapter = chaptersAdapter
        binding.rvAllCourses.addItemDecoration(VerticalItemDecoration(30))
        binding.rvAllCourses.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
         binding.rvAllCourses.isNestedScrollingEnabled = false
    }


}