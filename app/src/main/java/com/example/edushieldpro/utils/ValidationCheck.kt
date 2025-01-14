package com.example.edushieldpro.utils

import android.util.Patterns

fun validateEmail(email : String) : RegisterValidation {
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cant be empty")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email format")
    return RegisterValidation.Success
}

fun validatePassword(pass : String) : RegisterValidation {
    if(pass.isEmpty())
        return RegisterValidation.Failed("Password cant be empty")
    if(pass.length<6)
        return RegisterValidation.Failed("Password should contain at least 6 characters")
    return RegisterValidation.Success
}
fun validateRePassword(repass : String,pass : String) : RegisterValidation {
    if(repass.isEmpty())
        return RegisterValidation.Failed("Password cant be empty")
    if(repass.length<6)
        return RegisterValidation.Failed("Password should contain at least 6 characters")
    if(repass!=pass)
        return RegisterValidation.Failed("Password do not match")

    return RegisterValidation.Success
}

fun validateMobile(phone : String) : RegisterValidation {
    if(phone.isEmpty())
        return RegisterValidation.Failed("Password cant be empty")
    if(phone.length!=11)
        return RegisterValidation.Failed("Phone No should contain 11 characters")
    return RegisterValidation.Success
}

fun validateCnic(cnic : String) : RegisterValidation {
    if(cnic.isEmpty())
        return RegisterValidation.Failed("Cnic cant be empty")
    if(cnic.length !=13)
        return RegisterValidation.Failed("Cnic should contain 13 characters")
    return RegisterValidation.Success
}