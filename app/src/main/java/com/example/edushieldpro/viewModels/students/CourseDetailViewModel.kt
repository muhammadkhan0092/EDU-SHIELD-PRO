package com.example.edushieldpro.viewModels.students
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPurchaseHistory
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {



    private val _update = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val update : StateFlow<Resource<String>>
        get() = _update.asStateFlow()

    private val _payment = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val payment : StateFlow<Resource<String>>
        get() = _payment.asStateFlow()




    fun updateCourseAndSetHistory(course: Course,dataPurchaseHistory: DataPurchaseHistory){
        viewModelScope.launch {
            _update.emit(Resource.Loading())
        }
        userRepository.updateCourseAndSetHistory(
            course,
            dataPurchaseHistory,
            {
                viewModelScope.launch {
                    _update.emit(Resource.Success("DONE"))
                }
            },
            {
                viewModelScope.launch {
                    _update.emit(Resource.Error(it))
                }
            }
        )
    }

    fun payment(amount : Int){
        viewModelScope.launch {
            _payment.emit(Resource.Loading())
        }
        viewModelScope.launch {
            userRepository.payment(
                amount,
                {
                    viewModelScope.launch {
                        _payment.emit(Resource.Success(it))
                    }
                },
                {
                   viewModelScope.launch {
                       _payment.emit(Resource.Success(it))
                   }
                }
            )
        }


    }

}