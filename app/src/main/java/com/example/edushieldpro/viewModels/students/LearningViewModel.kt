package com.example.edushieldpro.viewModels.students
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.ProgressData
import com.example.edushieldpro.repo.UserRepository
import com.example.edushieldpro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getCourses = MutableStateFlow<Resource<MutableList<Course>>>(Resource.Unspecified())
    val getCourses : StateFlow<Resource<MutableList<Course>>>
        get() = _getCourses.asStateFlow()

    private val _completed = MutableStateFlow<Resource<ProgressData>>(Resource.Unspecified())
    val completed : StateFlow<Resource<ProgressData>>
        get() = _completed.asStateFlow()






    fun progress(progressData: ProgressData){
        viewModelScope.launch {
            _completed.emit(Resource.Loading())
        }
        userRepository.progress(
            progressData,
            {p->
                viewModelScope.launch {
                    _completed.emit(Resource.Success(p))
                }
            },
            {ERR->
                viewModelScope.launch {
                    _completed.emit(Resource.Error(ERR))
                }
            }
        )
    }

    fun getCourses(uuid: String){
        viewModelScope.launch {
            _getCourses.emit(Resource.Loading())
        }
        userRepository.getCourses(
            {
                val list : MutableList<Course> = mutableListOf()
                it.forEach {unitCourse->
                    if(uuid in unitCourse.students){
                        list.add(unitCourse)
                    }
                }
                viewModelScope.launch {
                    _getCourses.emit(Resource.Success(list))
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