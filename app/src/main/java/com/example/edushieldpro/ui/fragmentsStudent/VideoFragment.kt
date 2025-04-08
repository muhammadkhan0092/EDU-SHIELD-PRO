package com.example.edushieldpro.ui.fragmentsStudent

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.edushieldpro.databinding.VideoFragmentBinding
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.OtpResponse
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.models.VideoData
import com.example.edushieldpro.ui.activities.HomeActivity
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.viewModels.students.VideoFragmentViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.PlayerOption
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.PlayerHost
import com.vdocipher.aegis.player.VdoInitParams
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoTimeLine
import com.vdocipher.aegis.ui.view.VdoPlayerUIFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoFragment : Fragment() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: VideoFragmentBinding
    private lateinit var video:VideoData
    private val navArgs by navArgs<VideoFragmentArgs>()
    private val viewModel by viewModels<VideoFragmentViewModel>()
    private lateinit var course : Course
    private lateinit var otpData: OtpResponse
    private lateinit var vd: VdoPlayer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VideoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getVideoAndSendOtpRequest()
        observeOtp()
        observeUpdate()
        (activity as HomeActivity).binding.bnbStudent.visibility = View.GONE
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            vd.stop()
            val seconds = (lastWatchedPosition / 1000) % 60
            val minutes = (lastWatchedPosition / (1000 * 60)) % 60
            val timeData = TimeData(minutes.toInt(),seconds.toInt())
            viewModel.update(video.videoId,timeData)
        }
    }





    private fun observeUpdate() {
        lifecycleScope.launch {
            viewModel.update.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Log.d("khan","new ${it.message}")
                        binding.progressBar8.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.progressBar8.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        val data = it.data
                       course.videos.remove(video)
                        data?.singleProgress?.forEach {
                            if(it.videoId == video.videoId){
                                video.watchedData = it.timeData
                            }
                        }
                        course.videos.add(video)
                        binding.progressBar8.visibility = View.INVISIBLE
                        val bundle = Bundle().also {
                            it.putParcelable("Course",course)
                            it.putString("Status","0")
                            it.putString("from","video")
                            it.putParcelable("progress",data)
                        }
                        findNavController().navigate(com.example.edushieldpro.R.id.action_videoFragment_to_courseDetailFragment,bundle)
                    }
                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun observeOtp() {
        lifecycleScope.launch {
            viewModel.getOtp.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        binding.progressBar8.visibility = View.INVISIBLE
                    }

                    is Resource.Loading -> {
                        binding.progressBar8.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        otpData = it.data!!
                        Log.d("khan","otp is ${otpData}")
                        val playerFragment =
                            childFragmentManager.findFragmentById(com.example.edushieldpro.R.id.vdo_player_fragment) as VdoPlayerUIFragment
                        playerFragment.initialize(initializationListener)
                        binding.progressBar8.visibility = View.INVISIBLE
                    }

                    is Resource.Unspecified -> {

                    }
                }
            }
        }
    }

    private fun getVideoAndSendOtpRequest() {
        video = navArgs.video
        course = navArgs.course
        viewModel.getOtp(
            video.videoId
        )
    }

    // Store last watched position
    private var lastWatchedPosition: Long = 0

    val initializationListener = object : PlayerHost.InitializationListener {
        override fun onInitializationSuccess(
            playerHost: PlayerHost,
            vdoPlayer: VdoPlayer,
            wasRestored: Boolean
        ) {
            val seconds = video.watchedData.seconds * 1000 + video.watchedData.minutes*60*1000
            val vdoInitParams = VdoInitParams.Builder()
                .setOtp(otpData.otp)
                .setPlaybackInfo(otpData.playbackInfo)
                .setResumeTime(seconds)
                .build()
            vdoPlayer.load(vdoInitParams)
            vd = vdoPlayer
            // Listen for playback progress
            vdoPlayer.addPlaybackEventListener(object : VdoPlayer.PlaybackEventListener {
                override fun onProgress(positionMs: Long) {
                    lastWatchedPosition = positionMs
                    Log.d("VideoStats", "Current Position: $positionMs ms")
                }

                override fun onMediaEnded(p0: VdoInitParams?) {
                    Log.d("VideoStats", "Video fully watched! Total Duration: $lastWatchedPosition ms")
                }

                override fun onError(p0: VdoInitParams?, p1: ErrorDescription?) {
                    Log.e("VideoStats", "Playback error: ${p1?.errorMsg}")
                }

                override fun onLoadError(p0: VdoInitParams?, p1: ErrorDescription?) {
                    Log.e("VideoStats", "Loading error: ${p1?.errorMsg}")
                }

                override fun onPlayerStateChanged(isPlaying: Boolean, state: Int) {
                    when (state) {
                        VdoPlayer.STATE_READY -> Log.d("VideoStats", "Player is ready")
                        VdoPlayer.STATE_BUFFERING -> Log.d("VideoStats", "Buffering...")
                        VdoPlayer.STATE_ENDED -> Log.d("VideoStats", "Playback ended")
                    }
                }

                override fun onSeekTo(positionMs: Long) {}
                override fun onBufferUpdate(bufferTimeMs: Long) {}
                override fun onPlaybackSpeedChanged(speed: Float) {}
                override fun onLoading(p0: VdoInitParams?) {}
                override fun onLoaded(p0: VdoInitParams?) {}
                override fun onTracksChanged(p0: Array<out Track>?, p1: Array<out Track>?) {}
                override fun onMetaDataLoaded(p0: PlayerOption?) {}
                override fun onTimelineChanged(p0: VdoTimeLine?, p1: Int) {}
            })
        }

        override fun onInitializationFailure(
            playerHost: PlayerHost,
            errorDescription: ErrorDescription
        ) {
            Log.d("VideoStats", "Player initialization failed: ${errorDescription.errorMsg}")
        }

        override fun onDeInitializationSuccess() {
            Log.d("VideoStats", "Video closed. Watched time: $lastWatchedPosition ms")
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("VideoStats", "User closed video. Watched: $lastWatchedPosition ms")
    }

    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }





}


