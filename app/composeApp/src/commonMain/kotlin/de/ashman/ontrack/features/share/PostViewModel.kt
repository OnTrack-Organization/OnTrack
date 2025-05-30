package de.ashman.ontrack.features.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.network.services.share.PostService
import de.ashman.ontrack.network.services.share.dto.CreateCommentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostViewModel(
    private val postService: PostService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState: StateFlow<PostUiState> = _uiState

    private var postsPage = 0
    private var commentsPage = 0
    private var likesPage = 0
    private val pageSize = 10

    init {
        fetchPosts(initial = true)
    }

    fun fetchPosts(initial: Boolean = false) = viewModelScope.launch {
        if (initial) {
            postsPage = 0
            _uiState.update { it.copy(loading = true) }
        } else {
            if (!_uiState.value.canLoadMore) return@launch
            _uiState.update { it.copy(loadingMore = true) }
        }

        //delay(3000)

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
                _uiState.update { it.copy(selectedPost = post) }
                Logger.d { "Fetched post: $post" }
            },
            onFailure = {
                _uiState.update { it.copy(resultState = PostResultState.Error, selectedPost = null) }
                Logger.e { "Failed to fetch post: ${it.message}" }
            }
        )
    }

    // TODO maybe change these type of methods to return the complete new postdto instead
    fun fetchComments(postId: String, initial: Boolean = false) = viewModelScope.launch {
        if (initial) commentsPage = 0

        postService.getComments(postId, commentsPage, pageSize).fold(
            onSuccess = { newComments ->
                _uiState.update { state ->
                    val currentPost = state.selectedPost ?: state.posts.find { it.id == postId }
                    val updatedComments = if (initial) newComments else currentPost?.comments.orEmpty() + newComments

                    val updatedPost = currentPost?.copy(comments = updatedComments)

                    state.copy(
                        resultState = PostResultState.Success,
                        selectedPost = updatedPost,
                    )
                }

                Logger.d { "Fetched comments (size: ${newComments.size}, page: $commentsPage, initial: $initial): $newComments" }
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
                _uiState.update { state ->
                    val currentPost = state.selectedPost ?: state.posts.find { it.id == postId }
                    val updatedLikes = if (initial) newLikes else currentPost?.likes.orEmpty() + newLikes

                    val updatedPost = currentPost?.copy(likes = updatedLikes)

                    state.copy(selectedPost = updatedPost)
                }

                Logger.d { "Fetched likes (size: ${newLikes.size}, page: $likesPage, initial: $initial): $newLikes" }
                //likesPage++
            },
            onFailure = {
                Logger.e { "Failed to fetch likes: ${it.message}" }
            }
        )
    }

    // TODO ugly, change it!
    fun toggleLike(postId: String) = viewModelScope.launch {
        postService.toggleLike(postId).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Toggled like, updated post: $updatedPost" }
            },
            onFailure = {
                Logger.e { "Failed to toggle like: ${it.message}" }
            }
        )
    }

    fun addComment(commentText: String, mentionedUsers: List<String>) = viewModelScope.launch {
        val postId = _uiState.value.selectedPost?.id ?: return@launch
        val dto = CreateCommentDto(mentionedUsers = mentionedUsers, message = commentText)

        postService.addComment(postId, dto).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Added comment, updated post: $updatedPost" }
            },
            onFailure = {
                Logger.e { "Failed to add comment: ${it.message}" }
            }
        )
    }

    fun removeComment(commentId: String) = viewModelScope.launch {
        val postId = _uiState.value.selectedPost?.id ?: return@launch

        postService.deleteComment(postId, commentId).fold(
            onSuccess = { updatedPost ->
                replacePost(updatedPost)
                Logger.d { "Removed comment, updated post: $updatedPost" }
            },
            onFailure = {
                Logger.e { "Failed to remove comment: ${it.message}" }
            }
        )
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
        _uiState.update { it.copy(selectedPost = post) }
    }

    fun clearViewModel() {}
}

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val resultState: PostResultState = PostResultState.Empty,
    val loading: Boolean = false,
    val loadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
)

enum class PostResultState {
    Success,
    Error,
    Empty,
}
