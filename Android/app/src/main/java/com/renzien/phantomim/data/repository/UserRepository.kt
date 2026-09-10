package com.renzien.phantomim.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.renzien.phantomim.data.model.UserProfile
import java.util.Locale
import kotlinx.coroutines.tasks.await

class UsernameTakenException : Exception(
    "This username is already taken."
)

class UserRepository(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {
    private val usernamePattern = Regex("[A-Za-z0-9_]{3,20}")

    suspend fun getProfile(uid: String): UserProfile? {
        require(uid.isNotBlank()) {
            "User ID must not be blank."
        }

        val snapshot = firestore
            .collection("users")
            .document(uid)
            .get(Source.SERVER)
            .await()

        if (!snapshot.exists()) {
            return null
        }

        val storedUid = checkNotNull(snapshot.getString("uid")) {
            "User profile is missing its user ID."
        }

        check(storedUid == uid) {
            "User profile ID does not match."
        }

        return UserProfile(
            uid = storedUid,
            username = checkNotNull(
                snapshot.getString("username")
            ) {
                "User profile is missing its username."
            },
            usernameKey = checkNotNull(
                snapshot.getString("usernameKey")
            ) {
                "User profile is missing its username key."
            },
            createdAt = snapshot.getTimestamp("createdAt")
        )
    }

    suspend fun findProfileByUsername(
        username: String
    ): UserProfile? {
        val cleanUsername = username
            .trim()
            .removePrefix("@")

        require(usernamePattern.matches(cleanUsername)) {
            "Username must contain 3 to 20 letters, numbers, or underscores."
        }

        val usernameKey = cleanUsername.lowercase(Locale.ROOT)

        val usernameSnapshot = firestore
            .collection("usernames")
            .document("u_$usernameKey")
            .get(Source.SERVER)
            .await()

        if (!usernameSnapshot.exists()) {
            return null
        }

        val ownerUid = checkNotNull(
            usernameSnapshot.getString("uid")
        ) {
            "Username claim is missing its user ID."
        }

        val profile = checkNotNull(getProfile(ownerUid)) {
            "Username claim has no matching user profile."
        }

        check(profile.usernameKey == usernameKey) {
            "Username claim does not match the user profile."
        }

        return profile
    }

    suspend fun createProfileIfMissing(
        uid: String,
        username: String
    ) {
        require(uid.isNotBlank()) {
            "User ID must not be blank."
        }

        val cleanUsername = username.trim()

        require(usernamePattern.matches(cleanUsername)) {
            "Username must contain 3 to 20 letters, numbers, or underscores."
        }

        val usernameKey = cleanUsername.lowercase(Locale.ROOT)

        val profileReference = firestore
            .collection("users")
            .document(uid)

        val usernameReference = firestore
            .collection("usernames")
            .document("u_$usernameKey")

        firestore.runTransaction { transaction ->
            val profileSnapshot = transaction.get(
                profileReference
            )

            // Keep the existing profile when an operation is retried.
            if (profileSnapshot.exists()) {
                return@runTransaction Unit
            }

            val usernameSnapshot = transaction.get(
                usernameReference
            )

            if (usernameSnapshot.exists()) {
                throw UsernameTakenException()
            }

            val profileData = mapOf(
                "uid" to uid,
                "username" to cleanUsername,
                "usernameKey" to usernameKey,
                "createdAt" to FieldValue.serverTimestamp()
            )

            val usernameData = mapOf(
                "uid" to uid
            )

            transaction.set(profileReference, profileData)
            transaction.set(usernameReference, usernameData)

            Unit
        }.await()
    }
}