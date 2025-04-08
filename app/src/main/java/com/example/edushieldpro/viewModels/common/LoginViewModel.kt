package com.example.edushieldpro.viewModels.common
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.models.User
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.RegisterFieldState
import com.example.edushieldpro.utils.RegisterValidation
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.utils.validateEmail
import com.example.edushieldpro.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _usersList = MutableStateFlow<Resource<List<User>>>(Resource.Unspecified())
    val usersList : StateFlow<Resource<List<User>>>
        get() = _usersList.asStateFlow()

    private val _login = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val login : StateFlow<Resource<String>>
        get() = _login.asStateFlow()

    private val _updateUser = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateUser : StateFlow<Resource<User>>
        get() = _updateUser.asStateFlow()

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()


    fun getUsers(type:String){
        viewModelScope.launch {
            _usersList.emit(Resource.Loading())
        }
        userRepository.getUsers(
            type,
            {
                viewModelScope.launch {
                    _usersList.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _usersList.emit(Resource.Error(it))
                }
            }
        )
    }

    fun updateUser(user: User){
        userRepository.updateUser(
            user,
            {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _updateUser.emit(Resource.Error(it))
                }
            }
        )
    }

    fun loginWithEmailAndPassword(email: String,pass: String){
        Log.d("khan","logging in")
        userRepository.logInUserWithEmailAndPassword(
            email,
            pass,
            {
                Log.d("khan","login horha he view model me")
                viewModelScope.launch {
                    _login.emit(Resource.Success("DONE"))
                }
            },
            {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it))
                }
            }
        )
    }
     fun checkValidation(email: String,pass : String) : Boolean {
        val emailValidation = validateEmail(email)
        val passValidation = validatePassword(pass)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passValidation is RegisterValidation.Success
        return shouldRegister
    }
}