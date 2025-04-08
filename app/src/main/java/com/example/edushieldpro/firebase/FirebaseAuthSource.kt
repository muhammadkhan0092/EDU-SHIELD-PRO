package com.example.edushieldpro.firebase

import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.edushieldpro.api.RetrofitInstance
import com.example.edushieldpro.models.Course
import com.example.edushieldpro.models.DataPurchaseHistory
import com.example.edushieldpro.models.PaymentIntentResponse
import com.example.edushieldpro.models.ProgressData
import com.example.edushieldpro.models.SingleProgress
import com.example.edushieldpro.models.TimeData
import com.example.edushieldpro.models.User
import com.example.edushieldpro.models.VideoData
import com.example.edushieldpro.models.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.UUID

class FirebaseAuthSource {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()




    fun getUsers(
        type: String,
        onSuccess: (List<User>) -> Unit,
        onError:(String) -> Unit
    ){
        firestore.collection(type).get(Source.SERVER)
            .addOnSuccessListener {
                val list : MutableList<User> = mutableListOf()
              it.forEach {
                  val user = it.toObject(User::class.java)
                  Log.d("khan","users is ${user}")
                  if(user!=null){
                      list.add(user)
                  }
              }
                onSuccess(list)
                Log.d("khan","SUCCESS")
            }
            .addOnFailureListener {
                onError(it.message.toString())
                Log.d("khan","ERROR ${it.message.toString()}")
            }
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
        deviceId : String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    Log.d("khan","auth successfull")
                    val uiid = firebaseAuth.currentUser?.uid
                    firestore.runBatch {
                        if (uiid != null) {
                            val user = User(uiid,name,email,password,cnic,mobileNo,status,deviceId,type,"","",0)
                            firestore.collection(type).document(uiid).set(user)
                                .addOnSuccessListener {
                                Log.d("khan","onSuccess listener of firestore")
                            }
                                .addOnFailureListener {
                                    Log.d("khan","onFailure listener of firestore")
                                }
                        }
                    }.addOnSuccessListener {
                        Log.d("khan","on Success of batch")
                        onSuccess()
                    }
                        .addOnFailureListener {
                            Log.d("khan","on Failure of batch")
                            if (uiid != null) {
                                rollbackUser(uiid,onError)
                            }
                        }
                } else {
                    Log.d("khan","auth failed")
                    onError(authTask.exception?.message ?: "Unknown Auth Error")
                }
            }
    }

    fun logInUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,

    ) {
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                Log.d("khan","error horha he")
                onError(it.message.toString())
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

    fun updateStudentsInCourse(
        course: Course,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        val reference = firestore.collection("courses").document(course.courseId)
        reference.update(
            "students", course.students
        )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
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
    ) {
        val reference = firestore.collection("courses").document()
        val course = Course(reference.id,firebaseAuth.uid.toString(),title,category,image,rating,sold,price,description,videos,students,0,jazzCash)
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

    fun updateUser(user: User,onSuccess: (User) -> Unit,onError: (String) -> Unit){
        firestore.collection(user.type).document(user.uuid).set(user)
            .addOnSuccessListener {
                onSuccess(user)
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    fun courseCompleted(data : ProgressData,onSuccess: (ProgressData) -> Unit,onError: (String) -> Unit){
        firestore.collection("progress").document(firebaseAuth.uid!!)
            .get(Source.SERVER)
            .addOnSuccessListener {
               val obj = it.toObject(ProgressData::class.java)
                Log.d("khan","data fetched successfully ${obj}")
                if(obj==null || obj.uuid.isNullOrBlank()){
                    setCourseProgress(data,onSuccess,onError)
                }
                else
                {
                    val singleProgress = findCommonSingleProgress(data,obj)
                    Log.d("khan","size is ${singleProgress.size}")
                    if(singleProgress.size == data.singleProgress.size){
                        Log.d("khan","already")
                        onSuccess(obj)
                    }
                    else
                    {
                        obj.singleProgress.forEach {
                            if(it in singleProgress){
                                Log.d("khan","Already there")
                            }
                            else
                            {
                                data.singleProgress.add(it)
                            }
                        }
                        Log.d("khan","adding new data ${data}")
                        setCourseProgress(data,onSuccess,onError)
                    }
                }
            }
            .addOnFailureListener {
                setCourseProgress(data,onSuccess,onError)
            }
    }

    fun findCommonSingleProgress(data1: ProgressData, data2: ProgressData): List<SingleProgress> {
        val videoIds1 = data1.singleProgress.map { it.videoId }.toSet()
        return data2.singleProgress.filter { it.videoId in videoIds1 }
    }



    private fun setCourseProgress(data: ProgressData,onSuccess: (ProgressData) -> Unit,onError: (String) -> Unit) {
        firestore.collection("progress").document(firebaseAuth.uid!!)
            .set(data)
            .addOnSuccessListener {
                onSuccess(data)
                Log.d("khan","data setting successfull ")
            }
            .addOnFailureListener {
                onError(it.message.toString())
                Log.d("khan","data setting faied")
            }
    }

     fun getVideoWatched(videoId : String,timeData: TimeData,onSuccess: (ProgressData) -> Unit,onError: (String) -> Unit) {
        firestore.collection("progress").document(firebaseAuth.uid!!)
            .get()
            .addOnSuccessListener {
                val obj = it.toObject(ProgressData::class.java)
                if(obj!=null){
                   obj.singleProgress.forEach {single->
                       if(single.videoId==videoId){
                           single.timeData = timeData
                           setVideoWatched(obj,onSuccess,onError)
                       }
                   }
                }
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    private fun setVideoWatched(obj: ProgressData,onSuccess: (ProgressData) -> Unit,onError: (String) -> Unit) {
        firestore.collection("progress").document(firebaseAuth.uid!!)
            .set(obj)
            .addOnSuccessListener {
                onSuccess(obj)
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    fun getUser(type: String ,onSuccess: (User) -> Unit, onError: (String) -> Unit){
        firestore.collection(type).document(firebaseAuth.uid.toString())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if(user!=null){
                    onSuccess(user)
                }
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    fun setUser(type:String,user:User ,onSuccess: (User) -> Unit, onError: (String) -> Unit){
        firestore.collection(type).document(firebaseAuth.uid.toString())
            .set(user)
            .addOnSuccessListener {
               onSuccess(user)
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    fun getAndSetUser(
        type: String,
        uuid: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        val ref = firestore.collection(type).document(uuid)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)

            if (!snapshot.exists()) {
                throw Exception("User not found")
            }

            val user = snapshot.toObject(User::class.java) ?: throw Exception("Invalid user data")

            // Update courses count safely
            user.courses = (user.courses ?: 0) + 1

            // Set updated user data
            transaction.set(ref, user)

            // Must return user to be passed to `addOnSuccessListener`
            user
        }
            .addOnSuccessListener { updatedUser ->
                onSuccess(updatedUser)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Transaction failed")
            }
    }


    fun signOut() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun getRandomId() : String{
        return UUID.randomUUID().toString()
    }

    fun uploadBitmapToCloudinary(
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        isProfile: Boolean,
    ) {

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        MediaManager.get().upload(byteArray).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    if (isProfile) {
                       onSuccess(downloadUrl)
                    }
                } else {
                    Log.e("Cloudinary", "Error: Download URL not found")
                }
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                // You can implement progress tracking here if needed
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.d("Cloudinary","error")
                onError(error.toString())
            }

            override fun onStart(requestId: String?) {

            }
        }).dispatch()
    }

    fun uploadToCloudinary(filepath: String,onSuccess: (String) -> Unit,onError: (String) -> Unit) {
        MediaManager.get().upload(filepath).callback(object : UploadCallback {
            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                val downloadUrl = resultData?.get("url") as? String
                if (downloadUrl != null) {
                    onSuccess(downloadUrl)
                    Log.d("Cloudinary", "Download URL: $downloadUrl")
                } else {
                }
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                onError(error.toString())
            }

            override fun onStart(requestId: String?) {
            }
        }).dispatch()
    }

    val messagesCollection = firestore.collection("chats").document(firebaseAuth.uid.toString())
    fun retrieveMessages(onSuccess: (MessageModel) -> Unit, onError: (String) -> Unit) {
        var messageListenerRegistration: ListenerRegistration? = null
        messageListenerRegistration = messagesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message.toString())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("khan","retreived succesfully")
                    val messages = snapshot.toObject(MessageModel::class.java)
                    if(messages!=null){
                        val msges = messages.messages
                        onSuccess(messages)
                    }
                    else
                    {
                        createChat(onSuccess,onError)
                    }

                } else {
                    Log.d("khan","snapshot is null creating")
                   createChat(onSuccess,onError)
                }
            }
    }

    fun addNewMessage(messageModel : MessageModel, onSuccess: (MessageModel) -> Unit, onError: (String) -> Unit) {
        messagesCollection.set(messageModel)
            .addOnSuccessListener {
                onSuccess(messageModel)
            }
            .addOnFailureListener { error ->
                onError(error.message.toString())
                Log.d("khan","message not sent")
            }
    }



    private fun createChat(
        onSuccess: (MessageModel) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = firebaseAuth.uid.toString()
        var messageModel  = MessageModel()
        val userRef = firestore.collection("student").document(firebaseAuth.uid.toString())
        firestore.runTransaction {trans->
            val snap = trans.get(userRef)
            val data = snap.toObject(User::class.java)
            if(data!=null){
                messageModel = MessageModel(uid,uid, emptyList(),data.image)
                val ref = firestore.collection("chats").document(uid)
                trans.set(ref,messageModel)
            }
            else
            {
                onError("Error Creating chat")
            }
        }
            .addOnSuccessListener {
                onSuccess(messageModel)
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }



    }

    fun createPayment(amount: Int, onSuccess: (String) -> Unit,onError: (String) -> Unit) {
        RetrofitInstance.StripeApi.createPaymentIntent(amount)
            .enqueue(object : Callback<PaymentIntentResponse> {
                override fun onResponse(
                    call: Call<PaymentIntentResponse>,
                    response: Response<PaymentIntentResponse>
                ) {
                    if (response.isSuccessful) {
                        val clientSecret = response.body()?.clientSecret
                        if (clientSecret != null) {
                            Log.e("PaymentViewModel", "Success: Client Secret = $clientSecret")
                            onSuccess(clientSecret)
                        } else {
                            Log.e("PaymentViewModel", "Error: Client secret is null")
                            onError("Client secret is null")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("PaymentViewModel", "Error Response: $errorBody")
                        onError(errorBody ?: "Unknown error")
                    }
                }


                override fun onFailure(call: Call<PaymentIntentResponse>, t: Throwable) {
                    Log.e("PaymentViewModel", "Failure: ${t.message}")
                    onError(t.message.toString())
                }
            })
    }

    fun updateCourseAndSetHistory(
       course: Course,
       purchaseHistory: DataPurchaseHistory,
       onSuccess: () -> Unit,
       onError: (String) -> Unit
    ) {
        val refCourse = firestore.collection("courses").document(course.courseId)
        val refHistory = firestore.collection("history").document(purchaseHistory.id)

        firestore.runBatch { batch ->
            batch.set(refCourse,course)
            batch.set(refHistory,purchaseHistory)
        }
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }

    fun getPurchaseHistory(id:String,onSuccess: (MutableList<DataPurchaseHistory>) -> Unit,onError: (String) -> Unit){
        firestore.collection("history")
            .whereEqualTo("instructorId",id)
            .get()
            .addOnSuccessListener {
                val history = it.toObjects(DataPurchaseHistory::class.java)
                onSuccess(history)
            }.addOnFailureListener {
                onError(it.message.toString())
            }
    }

}
