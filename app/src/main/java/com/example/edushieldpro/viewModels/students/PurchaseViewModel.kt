package com.example.edushieldpro.viewModels.students
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getCourses = MutableStateFlow<Resource<MutableList<Course>>>(Resource.Unspecified())
    val getCourses : StateFlow<Resource<MutableList<Course>>>
        get() = _getCourses.asStateFlow()

    init {
        getCourses(FirebaseAuth.getInstance().uid.toString())
    }

    fun getCourses(uuid:String){
        viewModelScope.launch {
            _getCourses.emit(Resource.Loading())
        }
        userRepository.getCourses(
            {
                val list = it.filter { uuid !in it.students }
                viewModelScope.launch {
                    _getCourses.emit(Resource.Success(list.toMutableList()))
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