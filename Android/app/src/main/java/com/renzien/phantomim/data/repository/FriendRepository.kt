package com.renzien.phantomim.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.renzien.phantomim.data.model.AddFriendResult
import kotlinx.coroutines.tasks.await

class FriendRepository(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {
    suspend fun addFriend(
        ownerUid: String,
        friendUid: String
    ): AddFriendResult {
        require(ownerUid.isNotBlank()) {
            "Owner user ID must not be blank."
        }

        require(friendUid.isNotBlank()) {
            "Friend user ID must not be blank."
        }

        require(ownerUid != friendUid) {
            "You cannot add yourself as an ally."
        }

        val friendReference = firestore
            .collection("users")
            .document(ownerUid)
            .collection("friends")
            .document(friendUid)

        return firestore.runTransaction { transaction ->
            val friendSnapshot = transaction.get(
                friendReference
            )

            if (friendSnapshot.exists()) {
                return@runTransaction AddFriendResult.AlreadyAdded
            }

            val friendData = mapOf(
                "uid" to friendUid,
                "addedAt" to FieldValue.serverTimestamp()
            )

            transaction.set(
                friendReference,
                friendData
            )

            AddFriendResult.Added
        }.await()
    }
}