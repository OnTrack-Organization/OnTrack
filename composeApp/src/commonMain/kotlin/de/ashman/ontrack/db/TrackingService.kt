package de.ashman.ontrack.db

import de.ashman.ontrack.authentication.AuthService
import de.ashman.ontrack.domain.tracking.Tracking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class TrackingService(
    private val authService: AuthService,
    private val repository: TrackingRepository
) {
    private val _userId = MutableStateFlow(authService.currentUserId)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _trackings = MutableStateFlow<List<Tracking>>(emptyList())
    val trackings: StateFlow<List<Tracking>> = _trackings.asStateFlow()

    init {
        userId
            .filterNotNull()
            .flatMapLatest { repository.observeTrackings(it) }
            .onEach { _trackings.value = it }
            .launchIn(CoroutineScope(Dispatchers.IO + SupervisorJob()))
    }

    fun setUserId(id: String) {
        _userId.value = id
    }

    fun observeTrackingsForUser(userId: String): Flow<List<Tracking>> {
        return if (userId == _userId.value) {
            trackings
        } else {
            repository.observeTrackings(userId)
        }
    }
}

