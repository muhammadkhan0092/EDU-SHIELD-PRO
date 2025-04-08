package com.example.edushieldpro.viewModels.students
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
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getUser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val getUser : StateFlow<Resource<User>>
        get() = _getUser.asStateFlow()

    fun getUser(type : String){
        userRepository.getUser(
            type,
            {user->
                viewModelScope.launch {
                    _getUser.emit(Resource.Success(user))
                }
            },
            {
                viewModelScope.launch {
                    _getUser.emit(Resource.Error(it))
                }
            }
        )
    }





}