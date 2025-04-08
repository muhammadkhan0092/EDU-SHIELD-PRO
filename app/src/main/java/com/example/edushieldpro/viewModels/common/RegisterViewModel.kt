package com.example.edushieldpro.viewModels.common
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val register : StateFlow<Resource<String>>
        get() = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()


    fun createAccountWithEmailAndPassword(
        name: String,
        email: String,
        pass: String,
        cnic: String,
        phone: String,
        rePass: String,
        type: String,
        status: Int,
        deviceId: String
    ) {
        if (checkValidation(email,pass,cnic,phone, rePass)) {
            viewModelScope.launch {
                _register.emit(Resource.Loading())
            }
            userRepository.registerUser(
                name,
                email,
                pass,
                cnic,
                phone,
                {
                    viewModelScope.launch {
                        _register.emit(Resource.Success("Done"))
                    }
                },
                {
                    viewModelScope.launch {
                        _register.emit(Resource.Error(it))
                    }
                },
                type,
                status,
                deviceId
            )
        }
        else
        {
            val registerFieldState = RegisterFieldState(
                validateEmail(email),
                validatePassword(pass),
                validateRePassword(rePass,pass),
                validateCnic(cnic),
                validateMobile(phone)
            )
            viewModelScope.launch {
                _validation.send(registerFieldState)
            }
        }
    }



    private fun checkValidation(email: String,pass : String,cnic : String,phone : String,repass: String) : Boolean {
        val emailValidation = validateEmail(email)
        val passValidation = validatePassword(pass)
        val cnicValidation = validateCnic(cnic)
        val phoneValidation = validateMobile(phone)
        val rePassValidation = validateRePassword(repass,pass)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passValidation is RegisterValidation.Success
        return shouldRegister
    }
}