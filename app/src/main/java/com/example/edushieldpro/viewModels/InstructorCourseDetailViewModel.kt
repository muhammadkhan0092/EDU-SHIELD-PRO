package com.example.edushieldpro.viewModels
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
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
import com.example.edushieldpro.api.RetrofitInstance
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class InstructorCourseDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getVideos = MutableStateFlow<Resource<MutableList<Course>>>(Resource.Unspecified())
    val getVideos : StateFlow<Resource<MutableList<Course>>>
        get() = _getVideos.asStateFlow()

    private val _updateCourse = MutableStateFlow<Resource<Course>>(Resource.Unspecified())
    val updateCourse : StateFlow<Resource<Course>>
        get() = _updateCourse.asStateFlow()

    private val _uploadVideos = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val uploadVideos : StateFlow<Resource<String>>
        get() = _uploadVideos.asStateFlow()

    private val _cloudinary = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val cloudinary : StateFlow<Resource<String>>
        get() = _cloudinary.asStateFlow()


    suspend fun extractThumbnail(videoUri: Uri, contentResolver: ContentResolver): Bitmap? {
        return withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                // Open an AssetFileDescriptor from the URI using ContentResolver
                val assetFileDescriptor = contentResolver.openAssetFileDescriptor(videoUri, "r")
                assetFileDescriptor?.let {
                    retriever.setDataSource(it.fileDescriptor)
                    // Retrieve a frame at the 1-second mark or adjust as needed
                    val bitmap = retriever.getFrameAtTime(1000000) // 1 second = 1000000 microseconds
                    return@withContext bitmap
                }
                return@withContext null
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            } finally {
                retriever.release()
            }
        }
    }

     fun uploadVideoToServer(uri: Uri,context: Context,activity: Activity) {
         viewModelScope.launch {
             _uploadVideos.emit(Resource.Loading())
         }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.sendVideoDetails(
                    authorization = "Apisecret YrLwEOdIgab18bEOWHjR87V7PxCx2vZtenDBMJSaZR4UjotklK0c8IUrrVB9jdaQ",
                    title = "myFirstVideo",
                ).body()

                val uploadLink = response?.clientPayload?.uploadLink
                Log.d("khan", "Upload URL: ${response?.clientPayload}")
                val videoId = response?.videoId
                Log.d("khan", "Upload res: ${response?.videoId}")

                if (uploadLink != null) {
                    val inputStream = activity.contentResolver.openInputStream(uri)
                    if (inputStream == null) {
                        Log.e("khan", "Failed to open video stream")
                        return@launch
                    }
                    val requestBody = inputStream.use {
                        RequestBody.create("video/mp4".toMediaType(), it.readBytes())
                    }
                    val bodyMap = mapOf(
                        "x-amz-credential" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.xAmzCredential ?: ""),
                        "x-amz-algorithm" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.xAmzAlgorithm ?: ""),
                        "x-amz-date" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.xAmzDate ?: ""),
                        "x-amz-signature" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.xAmzSignature ?: ""),
                        "key" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.key ?: ""),
                        "policy" to RequestBody.create("text/plain".toMediaType(), response.clientPayload?.policy ?: ""),
                        "success_action_status" to RequestBody.create("text/plain".toMediaType(), "201"),
                        "success_action_redirect" to RequestBody.create("text/plain".toMediaType(), ""),
                        "file" to requestBody
                    )
                    val uploadResponse = RetrofitInstance.api.uploadFile(
                        uploadUrl = uploadLink,
                        body = bodyMap
                    )

                    if (uploadResponse.isSuccessful) {
                        viewModelScope.launch {
                            _uploadVideos.emit(Resource.Success(videoId!!))
                        }
                        Log.d("UploadDebug", "Upload successful!")
                        withContext(Dispatchers.Main) {
                            Log.d("khan","new res ${uploadResponse.body()}")
                        }
                    } else {
                        viewModelScope.launch {
                            _uploadVideos.emit(Resource.Error("upload Failed"))
                        }
                        Log.e("khan", "Upload failed: ${uploadResponse.message()}")
                        withContext(Dispatchers.Main) {
                            Log.d("khan","Upload Failed")
                        }
                    }
                } else {
                    viewModelScope.launch {
                        _uploadVideos.emit(Resource.Error("Upload Link is null"))
                    }
                    Log.e("khan", "Upload link is null")
                }
            } catch (e: Exception) {
                viewModelScope.launch {
                    _uploadVideos.emit(Resource.Error(e.message.toString()))
                }
                Log.e("khan", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    Log.d("khan","error ${e.message}")
                }
            }
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            _updateCourse.emit(Resource.Loading())
        }
        userRepository.updateCourse(
            course,
            {
                viewModelScope.launch {
                    _updateCourse.emit(Resource.Success(course))
                }
            },
            {
                viewModelScope.launch {
                    _updateCourse.emit(Resource.Error(it))
                }
            }
        )
    }

    fun uploadBitmapToCloudinary(bitmap: Bitmap, context: Context, onComplete: () -> Unit, isProfile: Boolean) {
        viewModelScope.launch {
            _cloudinary.emit(Resource.Loading())
        }
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        MediaManager.get().upload(byteArray).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    if (isProfile) {
                        viewModelScope.launch {
                            _cloudinary.emit(Resource.Success(downloadUrl))
                        }
                    }
                } else {
                    Log.e("Cloudinary", "Error: Download URL not found")
                }
                onComplete()
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                // You can implement progress tracking here if needed
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.d("Cloudinary","error")
                viewModelScope.launch {
                    _cloudinary.emit(Resource.Error("${error.toString()}"))
                }
                onComplete()
            }

            override fun onStart(requestId: String?) {
                // Handle any pre-upload scenarios if needed
            }
        }).dispatch()
    }

}