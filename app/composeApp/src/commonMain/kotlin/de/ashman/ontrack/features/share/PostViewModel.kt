package de.ashman.ontrack.features.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.datastore.UserDataStore
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.domain.user.User
import de.ashman.ontrack.features.common.CommonUiManager
import de.ashman.ontrack.network.services.report.ReportService
import de.ashman.ontrack.network.services.report.dto.ReportReason
import de.ashman.ontrack.network.services.report.dto.ReportRequestDto
import de.ashman.ontrack.network.services.report.dto.ReportType
import de.ashman.ontrack.network.services.share.PostService
import de.ashman.ontrack.network.services.share.dto.CreateCommentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ontrack.composeapp.generated.resources.Res
import ontrack.composeapp.generated.resources.post_add_comment_error
import ontrack.composeapp.generated.resources.post_remove_comment_error
import ontrack.composeapp.generated.resources.post_toggle_like_error
import ontrack.composeapp.generated.resources.report_error
import ontrack.composeapp.generated.resources.report_success

class PostViewModel(
    private val postService: PostService,
    private val reportService: ReportService,
    private val userDataStore: UserDataStore,
    private val commonUiManager: CommonUiManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState

    private var postsPage = 0
    private var commentsPage = 0
    private var likesPage = 0
    private val pageSize = 10

    init {
        viewModelScope.launch {
            userDataStore.currentUser.collect { user ->
                _uiState.update { state ->
                    state.copy(
                        currentUser = user,
                        isCurrentUserPost = (state.selectedPost?.user?.id == user?.id)
                    )
                }
            }
        }
    }

    fun fetchPosts(initial: Boolean = false) = viewModelScope.launch {
        if (initial) {
            postsPage = 0
            _uiState.update { it.copy(loading = true) }
        } else {
            if (!_uiState.value.canLoadMore) return@launch
            _uiState.update { it.copy(loadingMore = true) }
        }

        postService.getPosts(postsPage, pageSize).fold(
            onSuccess = { posts ->
                _uiState.update {
                    val updatedPosts = if (initial) posts else it.posts + posts
                    it.copy(
                        posts = updatedPosts,
                        resultState = if (updatedPosts.isEmpty()) PostResultState.Empty else PostResultState.Success,
                        canLoadMore = posts.size == pageSize
                    )
                }
                Logger.d { "Fetched posts (size: ${posts.size}, page: $postsPage, initial: $initial): $posts" }
                postsPage++
            },
            onFailure = {
                _uiState.update { it.copy(resultState = PostResultState.Error) }
                Logger.e { "Failed to fetch posts: ${it.message}" }
            }
        )

        _uiState.update { it.copy(loading = false, loadingMore = false) }
    }

    fun fetchPost(postId: String) = viewModelScope.launch {
        postService.getPost(postId).fold(
            onSuccess = { post ->
                setSelectedPost(post)
                Logger.d { "Fetched post: $post" }
            },
            onFailure = {
                _uiState.update { it.copy(resultState = PostResultState.Error, selectedPost = null) }
                Logger.e { "Failed to fetch post: ${it.message}" }
            }
        )
    }

    fun fetchComments(postId: String, initial: Boolean = false) = viewModelScope.launch {
        if (initial) commentsPage = 0

        postService.getComments(postId, likesPage, pageSize).fold(
            onSuccess = { newComments ->
                val currentPost = _uiState.value.selectedPost ?: _uiState.value.posts.find { it.id == postId }
                val updatedComments = if (initial) newComments.comments else currentPost?.comments.orEmpty() + newComments.comments

                val updatedPost = currentPost?.copy(
                    comments = updatedComments,
                    commentCount = newComments.commentCount,
                )

                if (updatedPost != null) {
                    replacePost(updatedPost)
                }

                Logger.d { "Fetched comments (size: ${newComments.comments.size}, page: $commentsPage, initial: $initial): $newComments" }
                //commentsPage++
            },
            onFailure = {
                Logger.e { "Failed to fetch comments: ${it.message}" }
            }
        )
    }

    fun fetchLikes(postId: String, initial: Boolean = false) = viewModelScope.launch {
        if (initial) likesPage = 0

        postService.getLikes(postId, likesPage, pageSize).fold(
            onSuccess = { newLikes ->
                val currentPost = _uiState.value.selectedPost ?: _uiState.value.posts.find { it.id == postId }
                val updatedLikes = if (initial) newLikes.likes else currentPost?.likes.orEmpty() + newLikes.likes

                val updatedPost = currentPost?.copy(
                    likes = updatedLikes,
                    likeCount = newLikes.likeCount,
                )

                if (updatedPost != null) {
                    replacePost(updatedPost)
                }

                Logger.d { "Fetched likes (size: ${newLikes.likes.size}, page: $likesPage, initial: $initial): $newLikes" }
                //likesPage++
            },
            onFailure = {
                Logger.e { "Failed to fetch likes: ${it.message}" }
            }
        )
    }

    fun toggleLike(postId: String) = viewModelScope.launch {
        postService.toggleLike(postId).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Toggled like, updated post: $updatedPost" }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.post_toggle_like_error)
                Logger.e { "Failed to toggle like: ${it.message}" }
            }
        )
    }

    fun addComment(commentText: String) = viewModelScope.launch {
        _uiState.update { it.copy(sendingComment = true) }

        val postId = _uiState.value.selectedPost?.id ?: return@launch
        val dto = CreateCommentDto(message = commentText)

        postService.addComment(postId, dto).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Added comment, updated post: $updatedPost" }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.post_add_comment_error)
                Logger.e { "Failed to add comment: ${it.message}" }
            }
        )

        _uiState.update { it.copy(sendingComment = false) }
    }

    fun removeComment(commentId: String) = viewModelScope.launch {
        val postId = _uiState.value.selectedPost?.id ?: return@launch

        postService.deleteComment(postId, commentId).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Removed comment, updated post: $updatedPost" }
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.post_remove_comment_error)
                Logger.e { "Failed to remove comment: ${it.message}" }
            }
        )
    }

    fun reportPost(reason: ReportReason, message: String?) = viewModelScope.launch {
        _uiState.update { it.copy(sendingReport = true) }
        val reportedId = _uiState.value.selectedPost?.user?.id ?: return@launch

        val dto = ReportRequestDto(
            reportedId = reportedId,
            type = ReportType.POST,
            reason = reason,
            message = message,
        )

        reportService.report(dto).fold(
            onSuccess = {
                commonUiManager.showSnackbar(Res.string.report_success)
            },
            onFailure = {
                commonUiManager.showSnackbar(Res.string.report_error)
            }
        )

        _uiState.update { it.copy(sendingReport = false) }
        commonUiManager.hideSheet()
    }

    private fun replacePost(updatedPost: Post) {
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { if (it.id == updatedPost.id) updatedPost else it },
                selectedPost = if (state.selectedPost?.id == updatedPost.id) updatedPost else state.selectedPost
            )
        }
    }

    fun setSelectedPost(post: Post) {
        val currentUserId = _uiState.value.currentUser?.id
        _uiState.update {
            it.copy(
                selectedPost = post,
                isCurrentUserPost = (post.user.id == currentUserId)
            )
        }
    }

    fun clearViewModel() {
        _uiState.update { PostUiState() }
    }
}

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val isCurrentUserPost: Boolean = false,
    val currentUser: User? = null,
    val resultState: PostResultState = PostResultState.Empty,
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val sendingComment: Boolean = false,
    val sendingReport: Boolean = false,
)

enum class PostResultState {
    Success,
    Error,
    Empty,
}
