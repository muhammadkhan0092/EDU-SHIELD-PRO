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
class CreateCourseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _createCourse = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val createCourse : StateFlow<Resource<String>>
        get() = _createCourse.asStateFlow()

    private val _cloudinary = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val cloudinary : StateFlow<Resource<String>>
        get() = _cloudinary.asStateFlow()


    fun createCourse(
        title : String,
        description : String,
        image : String,
        category : String,
        price : String
    ) {
        userRepository.createCourse(
            title,
            category,
            image,
            0.0,
            0,
            price,
            {
                viewModelScope.launch {
                    _createCourse.emit(Resource.Success("Created Successfully"))
                }
            },
            {
                viewModelScope.launch {
                    _createCourse.emit(Resource.Error(it))
                }
            },
            description
            )
    }



     fun checkValidation(
        title : String,
        description : String,
        image : String,
        category : String) : Boolean
    {
        if(title.isNullOrBlank() || description.isNullOrBlank() || image.isNullOrBlank() || category.isNullOrBlank()){
            return false
        }
        else
        {
            return true
        }
    }

    fun getRealPathFromUri(imageUri: Uri?, activity: Activity): String? {
        val cursor: Cursor? = activity.contentResolver.query(imageUri!!, null, null, null, null)
        return if (cursor == null) {
            imageUri.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }


    fun uploadToCloudinary(filepath: String, context: Context, onComplete: () -> Unit, isProfle : Boolean) {
        viewModelScope.launch {
            _cloudinary.emit(Resource.Loading())
        }
        MediaManager.get().upload(filepath).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    if(isProfle){
                        viewModelScope.launch {
                            _cloudinary.emit(Resource.Success(downloadUrl))
                        }
                    }
                    Log.d("Cloudinary", "Download URL: $downloadUrl")
                } else {
                }
                onComplete()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                viewModelScope.launch {
                    _cloudinary.emit(Resource.Error("${error.toString()}"))
                }
                onComplete()
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }


}