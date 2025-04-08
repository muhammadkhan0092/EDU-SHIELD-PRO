package com.example.edushieldpro.viewModels.students
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.api.RetrofitInstance
import com.example.edushieldpro.models.OtpResponse
import com.example.edushieldpro.models.ProgressData
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoFragmentViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _getOtp = MutableStateFlow<Resource<OtpResponse>>(Resource.Unspecified())
    val getOtp : StateFlow<Resource<OtpResponse>>
        get() = _getOtp.asStateFlow()

    private val _update = MutableStateFlow<Resource<ProgressData>>(Resource.Unspecified())
    val update : StateFlow<Resource<ProgressData>>
        get() = _update.asStateFlow()




     fun getOtp(videoId : String) {
         viewModelScope.launch {
             _getOtp.emit(Resource.Loading())
         }
         CoroutineScope(Dispatchers.IO).launch {
             try {
                 val response = RetrofitInstance.videoCipherApi.getOtp(videoId, "Apisecret OqmFoqrKcMMfqdvE22ptlKGvyqmGSo64Lz7XLqUYIqBppehUKoZw3PPhIGAr2Lby")
                 Log.d("UploadDebug", "Upload successful! OTP: ${response.otp}")
                 viewModelScope.launch {
                     _getOtp.emit(Resource.Success(response))
                 }
             } catch (e: Exception) {
                 viewModelScope.launch {
                     _getOtp.emit(Resource.Error(e.message.toString()))
                 }
                 Log.e("khan", "Error: ${e.localizedMessage}", e)
             }
         }
        }

    fun update(videoId:String,timeData: TimeData){
        viewModelScope.launch {
            _update.emit(Resource.Loading())
        }
        userRepository.updateWatchTime(
            videoId,
            timeData,
            {data->
                viewModelScope.launch {
                    _update.emit(Resource.Success(data))
                }
            },
            {err->
                viewModelScope.launch {
                    _update.emit(Resource.Error(err))
                }
            }
        )
    }
    }

