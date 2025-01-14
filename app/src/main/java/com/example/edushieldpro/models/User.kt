package com.example.edushieldpro.models

data class User(val name : String,val email : String, val password : String,val cnic : String,val phone : String){
    constructor() : this("","","","","")
}
