package com.example.edushieldpro.firebase

import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAuthSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()





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
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val uiid = firebaseAuth.currentUser?.uid
                    firestore.runBatch {
                        val user = User(name,email,password,cnic,mobileNo)
                        if (uiid != null) {
                            firestore.collection(type).document(uiid).set(user)
                        }
                    }.addOnSuccessListener {
                        onSuccess()
                    }
                        .addOnFailureListener {
                            if (uiid != null) {
                                rollbackUser(uiid,onError)
                            }
                        }
                } else {
                    onError(authTask.exception?.message ?: "Unknown Auth Error")
                }
            }
    }
    fun updateCourse(
        course: Course,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val reference = firestore.collection("courses").document(course.courseId)
        reference.update(
            "videos", course.videos
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
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
    ) {
        val reference = firestore.collection("courses").document()
        val course = Course(reference.id,firebaseAuth.uid.toString(),title,category,image,rating,sold,price,description)
        reference.set(course)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError("Could Not Create a course")
            }
    }

    fun getCourses(
        onSuccess: (MutableList<Course>) -> Unit,
        onError: (String) -> Unit,
    ){
        firestore.collection("courses").get()
            .addOnSuccessListener {
                val data = it.toObjects(Course::class.java)
                onSuccess(data)

            }
            .addOnFailureListener {
                onError("Could Not Get Data")
            }
    }


    private fun rollbackUser(uid: String?, onError: (String) -> Unit) {
        if (uid != null) {
            firebaseAuth.currentUser?.delete()
                ?.addOnSuccessListener {
                    onError("Firestore error occurred, user rolled back due to data inconsistency.")
                }
                ?.addOnFailureListener { e ->
                    onError("Rollback failed: ${e.message}")
                }
        } else {
            onError("Failed to rollback because user ID is null.")
        }
    }


    fun signOut() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null
}
