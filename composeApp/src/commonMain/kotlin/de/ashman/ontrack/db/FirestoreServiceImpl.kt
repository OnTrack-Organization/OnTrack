package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.db.entity.TrackingCommentEntity
import de.ashman.ontrack.db.entity.TrackingEntity
import de.ashman.ontrack.db.entity.UserEntity
import de.ashman.ontrack.domain.MediaType
import de.ashman.ontrack.domain.tracking.TrackStatus
import dev.gitlive.firebase.firestore.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock.System

class FirestoreServiceImpl(
    firestore: FirebaseFirestore,
    private val authService: AuthService,
) : FirestoreService {
    private val userCollection = firestore.collection("users")

    private fun userTrackingCollection(userId: String) = userCollection.document(userId).collection("trackings")

    // USER
    override suspend fun getUserById(userId: String): UserEntity? {
        return userCollection.document(userId).get().data()
    }

    override suspend fun addFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayUnion(friendId)
        )
    }

    override suspend fun removeFriend(friendId: String) {
        userCollection.document(authService.currentUserId).update(
            "friends" to FieldValue.arrayRemove(friendId)
        )
    }

    // TRACKING
    override suspend fun saveTracking(tracking: TrackingEntity) {
        userTrackingCollection(authService.currentUserId)
            .document(tracking.id)
            .set(tracking)
    }

    override suspend fun deleteTrackingsByMediaId(mediaId: String) {
        val documents = userTrackingCollection(authService.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .get()
            .documents

        documents.forEach {
            it.reference.delete()
        }
    }

    /*override fun fetchTrackings(userId: String): Flow<List<TrackingEntity>> = flow {
        val collection = userTrackingCollection(userId)
            .orderBy("timestamp", Direction.DESCENDING)

        val snapshot = try {
            collection.get(Source.CACHE)
        } catch (_: Exception) {
            collection.get()
        }

        val trackings = snapshot.documents
            .map { it.data<TrackingEntity>() }
            .groupBy { it.mediaId }
            .map { it.value.first() }

        emit(trackings)
    }*/

    override fun fetchTrackings(userId: String): Flow<List<TrackingEntity>> {
        return userTrackingCollection(userId)
            .orderBy("timestamp", Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents
                    .map { it.data<TrackingEntity>() }
                    .groupBy { it.mediaId }
                    .map { it.value.first() }
            }
    }

    // TODO probably not needed if above is implemented correctly
    override fun fetchTracking(mediaId: String): Flow<TrackingEntity?> {
        return userTrackingCollection(authService.currentUserId)
            .where { "mediaId" equalTo mediaId }
            .orderBy("timestamp", Direction.DESCENDING)
            .limit(1)
            .snapshots
            .map { it.documents.firstOrNull()?.data() }
    }

    // FEED
    override suspend fun getTrackingFeed(): List<TrackingEntity> {
        return listOf(
            TrackingEntity(
                id = "1",
                mediaType = MediaType.MOVIE,
                mediaCoverUrl = "https://image.tmdb.org/t/p/original/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
                mediaId = "1",
                mediaTitle = "Bestes Buch was jemals geschrieben wurde so guuuuuuuut",
                status = TrackStatus.CONSUMING,
                rating = 2,
                reviewTitle = "Test Title mit langem namen und so ja geil",
                reviewDescription = "Wow was ein guter titel , so viel spaß hat es gemacht zu lesen und ich mag es sehr und ja tschüss. Achso hier ist noch mehr text für dich",
                timestamp = System.now().toEpochMilliseconds(),
                userId = "2",
                username = "Ashman",
                userImageUrl = "bla",
                likedBy = listOf(),
                comments = listOf(
                    TrackingCommentEntity(
                        id = "1",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "2",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "3",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "4",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "5",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "6",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "7",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "8",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "9",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "10",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                ),
            ),
            TrackingEntity(
                id = "2",
                mediaType = MediaType.MOVIE,
                mediaCoverUrl = "https://image.tmdb.org/t/p/original/39wmItIWsg5sZMyRUHLkWBcuVCM.jpg",
                mediaId = "1",
                mediaTitle = "Bestes Buch was jemals geschrieben wurde so guuuuuuuut",
                status = TrackStatus.CONSUMING,
                rating = 2,
                reviewTitle = "Test Title mit langem namen und so ja geil",
                reviewDescription = "Wow was ein guter titel , so viel spaß hat es gemacht zu lesen und ich mag es sehr und ja tschüss. Achso hier ist noch mehr text für dich",
                timestamp = System.now().toEpochMilliseconds(),
                userId = "2",
                username = "Ashman",
                userImageUrl = "bla",
                likedBy = listOf(),
                comments = listOf(
                    TrackingCommentEntity(
                        id = "1",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "2",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "3",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "4",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "5",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "6",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "7",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "8",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "9",
                        userId = "1",
                        username = "Ashman",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                    TrackingCommentEntity(
                        id = "10",
                        userId = "1",
                        username = "Gerrit",
                        userImageUrl = "",
                        comment = "Coooooool comment and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff and cool stuff",
                        timestamp = System.now().toEpochMilliseconds(),
                    ),
                ),
            )
        )
    }

    override suspend fun likeTracking(friendId: String, trackingId: String) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likedBy" to FieldValue.arrayUnion(friendId)
            )
    }

    override suspend fun unlikeTracking(friendId: String, trackingId: String) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "likedBy" to FieldValue.arrayRemove(friendId)
            )
    }

    override suspend fun addComment(friendId: String, trackingId: String, comment: String) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayUnion(comment)
            )
    }

    override suspend fun deleteComment(friendId: String, trackingId: String, commentId: String) {
        userTrackingCollection(friendId)
            .document(trackingId)
            .update(
                "comments" to FieldValue.arrayRemove(commentId)
            )
    }
}
