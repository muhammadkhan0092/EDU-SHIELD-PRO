package com.example.edushieldpro.viewModels
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.RegisterFieldState
import com.example.edushieldpro.utils.RegisterValidation
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.validateCnic
import com.example.edushieldpro.utils.validateEmail
import com.example.edushieldpro.utils.validateMobile
import com.example.edushieldpro.utils.validatePassword
import com.example.edushieldpro.utils.validateRePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstructorCoursesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getCourses = MutableStateFlow<Resource<MutableList<Course>>>(Resource.Unspecified())
    val getCourses : StateFlow<Resource<MutableList<Course>>>
        get() = _getCourses.asStateFlow()

    init {
        getCourses()
    }

    fun getCourses(){
        viewModelScope.launch {
            _getCourses.emit(Resource.Loading())
        }
        userRepository.getCourses(
            {
                viewModelScope.launch {
                    _getCourses.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _getCourses.emit(Resource.Error(it))
                }
            }

        )
    }

}