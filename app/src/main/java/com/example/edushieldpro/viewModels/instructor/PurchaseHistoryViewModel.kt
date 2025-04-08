package com.example.edushieldpro.viewModels.instructor
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.edushieldpro.models.DataPurchaseHistory
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
class PurchaseHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _getHistory = MutableStateFlow<Resource<MutableList<DataPurchaseHistory>>>(Resource.Unspecified())
    val getHistory: StateFlow<Resource<MutableList<DataPurchaseHistory>>>
        get() = _getHistory.asStateFlow()

    fun getHistory(id:String){
        viewModelScope.launch {
            _getHistory.emit(Resource.Loading())
        }
        userRepository.getPurchaseHistory(
            id,
            {
                viewModelScope.launch {
                    _getHistory.emit(Resource.Success(it))
                }
            },
            {
                viewModelScope.launch {
                    _getHistory.emit(Resource.Error(it))
                }
            }

        )
    }




}