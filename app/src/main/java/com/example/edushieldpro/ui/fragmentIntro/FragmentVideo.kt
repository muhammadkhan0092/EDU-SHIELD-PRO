package com.example.edushieldpro.ui.fragmentIntro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.edushieldpro.api.RetrofitInstance
import com.example.edushieldpro.databinding.FragmentVideoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class FragmentVideo : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //    makeNetworkCall()
//        val playerFragment = childFragmentManager.findFragmentById(R.id.vdo_player_fragment) as VdoPlayerUIFragment
//        playerFragment.initialize(initializationListener)
        binding.btn.setOnClickListener {
            selectVideoFromDevice()
        }
    }


    val pickVideoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            Log.d("khan", "Selected URI: $uri")
            //uploadVideoToServer(uri!!)
        } else {
            Log.d("khan", "No video selected or error occurred")
        }
    }

    fun selectVideoFromDevice() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "video/*"
        }
        pickVideoLauncher.launch(intent)
    }


//
//
//    val initializationListener = object : PlayerHost.InitializationListener {
//        override fun onInitializationSuccess(
//            playerHost: PlayerHost,
//            vdoPlayer: VdoPlayer,
//            wasRestored: Boolean
//        ) {
//            // Build a video initialization params object to load the player with a video
//            val vdoInitParams = VdoInitParams.Builder()
//                .setOtp("20160313versASE323EDYIFtSYBPuAdnVYSpe2wlFzyHZkp4d4CxIN6qvqF4i6KL")
//                .setPlaybackInfo("eyJ2aWRlb0lkIjoiYmQ0MzBkNjQ2N2Q0NDQ4ZmEyMDE4NTdmMjBkYjI0YWQifQ==")
//                .build()
//
//            // Load the player with the VdoInitParams object
//            vdoPlayer.load(vdoInitParams)
//        }
//
//        override fun onInitializationFailure(
//            playerHost: PlayerHost,
//            errorDescription: ErrorDescription
//        ) {
//            Log.d("khan", "failed")
//        }
//
//        override fun onDeInitializationSuccess() {
//            // Called when the playerFragment is detached from the parent activity and the player is deinitialized.
//        }
//    https://www.vdocipher.com/api/videos//bd430d6467d4448fa201857f20db24ad/otp
//    https://dev.vdocipher.com/pLMsyQCyRUgps4Gt6kIm4JVDZpolKOFTVACMmPjhaVxs6P3xxsbImpduiqYGqNSU/videos?title=videotitle
//    pLMsyQCyRUgps4Gt6kIm4JVDZpolKOFTVACMmPjhaVxs6P3xxsbImpduiqYGqNSU


    //  }


    fun createStreamingRequestBody(inputStream: InputStream): RequestBody {
        return object : RequestBody() {
            override fun contentType() = "video/mp4".toMediaType()

            override fun writeTo(sink: BufferedSink) {
                inputStream.use { inStream ->
                    val buffer = ByteArray(8 * 1024) // 8KB buffer
                    var read: Int
                    while (inStream.read(buffer).also { read = it } != -1) {
                        sink.write(buffer, 0, read)
                    }
                }
            }
        }
    }




}
