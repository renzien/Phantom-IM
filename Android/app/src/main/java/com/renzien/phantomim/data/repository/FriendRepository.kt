package com.renzien.phantomim.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.renzien.phantomim.data.model.AddFriendResult
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.renzien.phantomim.data.model.FriendsPage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class FriendRepository(
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance(),
    private val userRepository: UserRepository =
        UserRepository(firestore)
) {

    suspend fun getFriendsPage(
        ownerUid: String,
        after: DocumentSnapshot? = null
    ): FriendsPage {
        require(ownerUid.isNotBlank()) {
            "Owner user ID must not be blank."
        }

        val pageSize = 20

        val friendsReference = firestore
            .collection("users")
            .document(ownerUid)
            .collection("friends")

        var query = friendsReference
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .limit((pageSize + 1).toLong())

        if (after != null) {
            require(
                after.reference.parent.path == friendsReference.path
            ) {
                "The cursor must belong to this user's contacts."
            }

            query = query.startAfter(after)
        }

        val snapshot = query
            .get(Source.SERVER)
            .await()

        val documents = snapshot.documents.take(pageSize)

        val profiles = coroutineScope {
            documents.map { document ->
                async {
                    val friendUid = checkNotNull(
                        document.getString("uid")
                    ) {
                        "Contact is missing its user ID."
                    }

                    check(friendUid == document.id) {
                        "Contact ID does not match its user ID."
                    }

                    checkNotNull(
                        userRepository.getProfile(friendUid)
                    ) {
                        "Contact has no matching user profile."
                    }
                }
            }.awaitAll()
        }

        return FriendsPage(
            profiles = profiles,
            nextCursor = if (snapshot.size() > pageSize) {
                documents.last()
            } else {
                null
            }
        )
    }
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