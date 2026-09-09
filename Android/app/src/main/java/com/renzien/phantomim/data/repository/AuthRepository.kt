package com.renzien.phantomim.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    suspend fun createAccount(
        email: String,
        password: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(
            email.trim(),
            password
        ).await()
    }

    suspend fun signIn(
        email: String,
        password: String
    ) {
        firebaseAuth.signInWithEmailAndPassword(
            email.trim(),
            password
        ).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}