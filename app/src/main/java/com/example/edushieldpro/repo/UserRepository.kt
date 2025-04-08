package com.example.edushieldpro.repo

import android.graphics.Bitmap
import com.example.edushieldpro.firebase.FirebaseAuthSource
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPurchaseHistory
import com.example.edushieldpro.models.ProgressData
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.models.User
import com.example.edushieldpro.models.VideoData
import com.example.edushieldpro.models.MessageModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource
) {

    fun getUsers(
        type:String,
        onSuccess: (List<User>) -> Unit,
        onError: (String) -> Unit,

        ){
        firebaseAuthSource.getUsers(type,onSuccess,onError)
    }

    fun logInUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,

        ) {
        firebaseAuthSource.logInUserWithEmailAndPassword(
            email,
            password,
            onSuccess,
            onError
        )

    }

    fun updateUser(user: User,onSuccess: (User) -> Unit,onError: (String) -> Unit){
        firebaseAuthSource.updateUser(user,onSuccess,onError)
    }


    fun registerUser(
        name: String,
        email: String,
        password: String,
        cnic: String,
        mobileNo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        type: String,
        status : Int,
        deviceId:String
    ) {
        firebaseAuthSource.registerUser(name,email, password, cnic, mobileNo, onSuccess, onError,type,status,deviceId)
    }

    fun createCourse(
        title: String = "",
        category: String = "",
        image: String = "",
        rating: Double = 0.0,
        sold: Int = 0,
        price: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        description: String,
        videos: MutableList<VideoData>,
        students: MutableList<String>,
        jazzCash: String
    ){
        firebaseAuthSource.createCourse(title,category,image,rating,sold,price,onSuccess,onError,description,videos,students,jazzCash)
    }

    fun updateCourse(
        course: Course,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ){
        firebaseAuthSource.updateCourse(course,onSuccess,onError)
    }

    fun updateStudents(
        course: Course,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ){
        firebaseAuthSource.updateStudentsInCourse(course,onSuccess,onError)
    }

    fun getCourses(
        onSuccess: (MutableList<Course>) -> Unit,
        onError: (String) -> Unit,
    ){
        firebaseAuthSource.getCourses(onSuccess,onError)
    }

    fun updateWatchTime(videoId: String,timeData: TimeData,onSuccess: (ProgressData) -> Unit, onError: (String) -> Unit){
        firebaseAuthSource.getVideoWatched(videoId,timeData,onSuccess,onError)
    }

    fun progress(progressData: ProgressData,onSuccess: (ProgressData) -> Unit,onError: (String) -> Unit){
        firebaseAuthSource.courseCompleted(progressData,onSuccess,onError)
    }
    fun getUser(type: String ,onSuccess: (User) -> Unit, onError: (String) -> Unit){
       firebaseAuthSource.getUser(type,onSuccess,onError)
    }

    fun setUser(type:String,user:User ,onSuccess: (User) -> Unit, onError: (String) -> Unit){
        firebaseAuthSource.setUser(type,user,onSuccess,onError)
    }

    fun getAndSetUsaer(type:String,uuid:String ,onSuccess: (User) -> Unit, onError: (String) -> Unit){
        firebaseAuthSource.getAndSetUser(type,uuid,onSuccess,onError)
    }
    fun uploadBitmapToCloudinary(
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        firebaseAuthSource.uploadBitmapToCloudinary(bitmap,onSuccess,onError,true)
    }

    fun uploadToCloudinary(
        filepath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        firebaseAuthSource.uploadToCloudinary(filepath,onSuccess,onError)
    }

    fun addNewMessage(messageModel: MessageModel, onSuccess: (MessageModel) -> Unit, onError: (String) -> Unit){
        firebaseAuthSource.addNewMessage(messageModel,onSuccess,onError)
    }
    fun retreiveMessages(onSuccess: (MessageModel) -> Unit, onError: (String) -> Unit){
        firebaseAuthSource.retrieveMessages(onSuccess,onError)
    }

    fun payment(amount:Int,onSuccess: (String) -> Unit,onError: (String) -> Unit){
        firebaseAuthSource.createPayment(amount,onSuccess,onError)
    }

    fun updateCourseAndSetHistory(course: Course,dataPurchaseHistory: DataPurchaseHistory,onSuccess: () -> Unit,onError: (String) -> Unit){
        firebaseAuthSource.updateCourseAndSetHistory(course,dataPurchaseHistory,onSuccess,onError)
    }

    fun getPurchaseHistory(id:String,onSuccess: (MutableList<DataPurchaseHistory>) -> Unit,onError: (String) -> Unit){
        firebaseAuthSource.getPurchaseHistory(id,onSuccess,onError)
    }
}
