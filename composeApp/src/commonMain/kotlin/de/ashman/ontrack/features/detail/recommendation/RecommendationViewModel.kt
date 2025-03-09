package de.ashman.ontrack.features.detail.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.ashman.ontrack.repository.firestore.FriendRepository
import de.ashman.ontrack.repository.firestore.RecommendationRepository
import de.ashman.ontrack.domain.media.Media
import de.ashman.ontrack.domain.recommendation.Recommendation
import de.ashman.ontrack.domain.recommendation.RecommendationStatus
import de.ashman.ontrack.domain.user.Friend
import de.ashman.ontrack.repository.CurrentUserRepository
import de.ashman.ontrack.notification.NotificationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.notifications_new_recommendation_body
import ontrack.composeapp.generated.resources.notifications_new_recommendation_title
import org.jetbrains.compose.resources.getString

class RecommendationViewModel(
    private val recommendationRepository: RecommendationRepository,
    private val friendRepository: FriendRepository,
    private val notificationService: NotificationService,
    private val currentUserRepository: CurrentUserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    private val previousRecommendationsCache = mutableMapOf<String, List<Recommendation>>()

    fun observeFriendRecommendations(mediaId: String) = viewModelScope.launch {
        recommendationRepository.fetchRecommendations(mediaId)
            .collect { recommendations ->
                _uiState.update { it.copy(receivedRecommendations = recommendations) }
            }
    }

    fun fetchFriends() = viewModelScope.launch {
        friendRepository.getFriends()
            .collect { friends ->
                _uiState.update { it.copy(friends = friends) }
            }
    }

    fun sendRecommendation(friendId: String, message: String?, media: Media) = viewModelScope.launch {
        val user = currentUserRepository.getCurrentUser()

        val recommendation = Recommendation(
            userId = user.id,
            userImageUrl = user.imageUrl,
            username = user.name,
            mediaId = media.id,
            mediaType = media.mediaType,
            mediaTitle = media.title,
            mediaCoverUrl = media.coverUrl,
            message = message,
            status = RecommendationStatus.PENDING
        )

        recommendationRepository.sendRecommendation(friendId, recommendation)

        val updatedRecs = listOf(recommendation) + (previousRecommendationsCache[friendId] ?: emptyList()).take(4)
        previousRecommendationsCache[friendId] = updatedRecs
        _uiState.update { it.copy(previousSentRecommendations = updatedRecs) }

        notificationService.sendPushNotification(
            userId = friendId,
            title = getString(Res.string.notifications_new_recommendation_title),
            body = getString(Res.string.notifications_new_recommendation_body, user.name, media.title),
            mediaId = media.id,
            imageUrl = media.coverUrl,
        )
    }

    fun passRecommendation(mediaId: String) = viewModelScope.launch {
        recommendationRepository.passRecommendation(mediaId)
    }

    fun selectFriend(friendId: String, mediaId: String) {
        // Clear the previous recommendations for the current mediaId to avoid showing old data
        _uiState.update { it.copy(previousSentRecommendations = emptyList()) }

        // Check if the cache contains recommendations for the given friendId and mediaId
        val cachedRecs = previousRecommendationsCache[friendId]

        // If the cache doesn't have recommendations for this friendId, fetch the previous recommendations
        if (cachedRecs == null || cachedRecs.none { it.mediaId == mediaId }) {
            // Cache is empty or doesn't contain recommendations for the given mediaId, so fetch them
            getPreviousSentRecommendations(friendId, mediaId)
        } else {
            // Use the cached recommendations
            _uiState.update { it.copy(previousSentRecommendations = cachedRecs) }
        }
    }

    fun getPreviousSentRecommendations(friendId: String, mediaId: String) = viewModelScope.launch {
        val recs = recommendationRepository.getPreviousSentRecommendations(friendId, mediaId)
        previousRecommendationsCache[friendId] = recs

        _uiState.update { it.copy(previousSentRecommendations = recs) }
    }

    fun clearRecommendations() {
        previousRecommendationsCache.clear()
        _uiState.update { RecommendationUiState() }
    }
}

data class RecommendationUiState(
    val friends: List<Friend> = emptyList(),
    val receivedRecommendations: List<Recommendation> = emptyList(),
    val previousSentRecommendations: List<Recommendation> = emptyList(),
)
