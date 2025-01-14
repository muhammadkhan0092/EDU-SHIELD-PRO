package com.example.edushieldpro.repo

import com.example.edushieldpro.firebase.FirebaseAuthSource
import com.example.edushieldpro.models.Course
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource
) {

    fun registerUser(
        name: String,
        email: String,
        password: String,
        cnic: String,
        mobileNo: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        type: String
    ) {
        firebaseAuthSource.registerUser(name,email, password, cnic, mobileNo, onSuccess, onError,type)
    }

    fun createCourse(
        title : String = "",
        category: String = "",
        image: String = "",
        rating: Double = 0.0,
        sold:Int = 0,
        price : String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        description : String
    ){
        firebaseAuthSource.createCourse(title,category,image,rating,sold,price,onSuccess,onError,description)
    }

    fun updateCourse(
        course: Course,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ){
        firebaseAuthSource.updateCourse(course,onSuccess,onError)
    }

    fun getCourses(
        onSuccess: (MutableList<Course>) -> Unit,
        onError: (String) -> Unit,
    ){
        firebaseAuthSource.getCourses(onSuccess,onError)
    }
}
