package com.example.edushieldpro.viewModels.common
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.models.User
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _update = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val update : StateFlow<Resource<User>>
        get() = _update.asStateFlow()

    private val _image = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val image : StateFlow<Resource<String>>
        get() = _image.asStateFlow()

    private val _getUser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val getUser : StateFlow<Resource<User>>
        get() = _getUser.asStateFlow()

    fun getUser(type:String){
        viewModelScope.launch {
            _getUser.emit(Resource.Loading())
        }
        userRepository.getUser(
            type,
            {
                viewModelScope.launch {
                    _getUser.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _getUser.emit(Resource.Error(it))
                }
            }
        )
    }

    fun uploadImage(filePath: String){
       viewModelScope.launch {
           _image.emit(Resource.Loading())
       }
        userRepository.uploadToCloudinary(
            filePath,
            {
                viewModelScope.launch {
                    _image.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _image.emit(Resource.Error(it))
                }
            }
            )
    }

    fun setUser(type:String,user: User){
        viewModelScope.launch {
            _update.emit(Resource.Loading())
        }
        userRepository.setUser(
            type,
            user,
            {
                viewModelScope.launch {
                    _update.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _update.emit(Resource.Error(it))
                }
            }
        )
    }





}