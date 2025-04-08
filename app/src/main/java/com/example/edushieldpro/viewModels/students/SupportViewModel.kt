package com.example.edushieldpro.viewModels.students
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import com.example.edushieldpro.models.MessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _add = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val add : StateFlow<Resource<MessageModel>>
        get() = _add.asStateFlow()

    private val _retreive = MutableStateFlow<Resource<MessageModel>>(Resource.Unspecified())
    val retreive : StateFlow<Resource<MessageModel>>
        get() = _retreive.asStateFlow()


    fun addNewMessage(messageModel: MessageModel){
        viewModelScope.launch {
            _add.emit(Resource.Loading())
        }
        userRepository.addNewMessage(
            messageModel,
            {
                viewModelScope.launch {
                    _add.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _add.emit(Resource.Error(it))
                }
            }
        )
    }

    fun retreiveMessages(){
        viewModelScope.launch {
            _retreive.emit(Resource.Loading())
        }
        userRepository.retreiveMessages(
            {
                viewModelScope.launch {
                    _retreive.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _retreive.emit(Resource.Error(it))
                }
            }
        )
    }
}