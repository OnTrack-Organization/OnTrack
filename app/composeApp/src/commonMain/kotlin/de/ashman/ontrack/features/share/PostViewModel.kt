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
            _uiState.update { it.copy(resultState = PostResultState.Loading) }
        } else {
            _uiState.update { it.copy(resultState = PostResultState.LoadingMore) }
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
                postsPage++
                Logger.d { "Fetched posts: $posts" }
            },
            onFailure = {
                _uiState.update { it.copy(resultState = PostResultState.Error) }
                Logger.e { "Failed to fetch posts: ${it.message}" }
            }
        )
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

    fun fetchComments(postId: String) = viewModelScope.launch {
        _uiState.update { state ->
            if (state.selectedPost?.id != postId) {
                val post = state.posts.find { it.id == postId }
                state.copy(selectedPost = post)
            } else state
        }

        postService.getComments(postId, commentsPage, pageSize).fold(
            onSuccess = { newComments ->
                _uiState.update { state ->
                    val updatedPost = state.selectedPost?.copy(comments = newComments)
                    state.copy(selectedPost = updatedPost)
                }

                // TODO add later
                //commentsPage++
                Logger.d { "Fetched comments: $newComments" }
            },
            onFailure = {
                Logger.e { "Failed to fetch comments: ${it.message}" }
            }
        )
    }

    // TODO maybe change these type of methods to return the complete new postdto instead
    fun fetchLikes(postId: String) = viewModelScope.launch {
        _uiState.update { state ->
            if (state.selectedPost == null) {
                val post = state.posts.find { it.id == postId }
                state.copy(selectedPost = post)
            } else state
        }

        postService.getLikes(postId, likesPage, pageSize).fold(
            onSuccess = { newLikes ->
                _uiState.update {
                    val updatedPost = it.selectedPost?.copy(likes = newLikes)

                    it.copy(selectedPost = updatedPost)
                }

                // TODO add later
                //likesPage++
                Logger.d { "Fetched likes: $newLikes" }
            },
            onFailure = {
                Logger.e { "Failed to fetch likes: ${it.message}" }
            }
        )
    }

    // TODO ugly, change it!
    fun toggleLike(postId: String) = viewModelScope.launch {
        postService.toggleLike(postId).fold(
            onSuccess = { like ->
                _uiState.update { state ->
                    val updatedPosts = state.posts.map { post ->
                        if (post.id != postId) return@map post

                        val wasLiked = post.likedByCurrentUser
                        val newLikeCount = if (wasLiked) post.likeCount - 1 else post.likeCount + 1

                        val updatedLikes = if (wasLiked) {
                            post.likes.filterNot { it.user.id == like.user.id }
                        } else {
                            (listOf(like) + post.likes).distinctBy { it.user.id }.take(3)
                        }

                        post.copy(
                            likedByCurrentUser = !wasLiked,
                            likeCount = newLikeCount,
                            likes = updatedLikes
                        )
                    }

                    val updatedSelectedPost = state.selectedPost?.let { post ->
                        if (post.id != postId) return@let post

                        val wasLiked = post.likedByCurrentUser

                        val updatedLikes = if (wasLiked) {
                            post.likes.filterNot { it.user.id == like.user.id }
                        } else {
                            (listOf(like) + post.likes).distinctBy { it.user.id }.take(3)
                        }

                        post.copy(
                            likedByCurrentUser = !wasLiked,
                            likes = updatedLikes
                        )
                    }

                    state.copy(
                        posts = updatedPosts,
                        selectedPost = updatedSelectedPost
                    )
                }

                Logger.d { "Toggled like: $like" }
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
            onSuccess = { comment ->
                _uiState.update { state ->
                    val updatedComments = state.selectedPost?.comments.orEmpty() + comment

                    val updatedSelectedPost = state.selectedPost?.copy(
                        comments = updatedComments,
                        commentCount = updatedComments.size
                    )

                    val updatedPosts = state.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(commentCount = updatedComments.size)
                        } else post
                    }

                    state.copy(
                        selectedPost = updatedSelectedPost,
                        posts = updatedPosts
                    )
                }

                Logger.d { "Added comment: $comment" }
            },
            onFailure = {
                Logger.e { "Failed to add comment: ${it.message}" }
            }
        )
    }

    fun removeComment(commentId: String) = viewModelScope.launch {
        val postId = _uiState.value.selectedPost?.id ?: return@launch

        postService.deleteComment(postId, commentId).fold(
            onSuccess = {
                _uiState.update { state ->
                    val updatedComments = state.selectedPost?.comments.orEmpty().filterNot { it.id == commentId }

                    val updatedSelectedPost = state.selectedPost?.copy(comments = updatedComments)

                    val updatedPosts = state.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(commentCount = updatedComments.size)
                        } else post
                    }

                    state.copy(
                        selectedPost = updatedSelectedPost,
                        posts = updatedPosts
                    )
                }

                Logger.d { "Removed comment: $commentId" }
            },
            onFailure = {
                Logger.e { "Failed to remove comment: ${it.message}" }
            }
        )
    }

    fun clearViewModel() {}
}

data class PostUiState(
    val posts: List<Post> = emptyList(),
    val selectedPost: Post? = null,
    val resultState: PostResultState = PostResultState.Loading,
    val canLoadMore: Boolean = false,
)

enum class PostResultState {
    Loading,
    Success,
    Error,
    Empty,
    LoadingMore
}
