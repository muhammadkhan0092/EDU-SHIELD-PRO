package com.example.edushieldpro.daggerHilt
import com.example.edushieldpro.firebase.FirebaseAuthSource
import com.example.edushieldpro.repo.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuthSource = FirebaseAuthSource()

    @Provides
    @Singleton
    fun provideUserRepository(firebaseAuthSource: FirebaseAuthSource): UserRepository =
        UserRepository(firebaseAuthSource)














}