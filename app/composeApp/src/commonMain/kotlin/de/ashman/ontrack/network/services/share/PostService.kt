package de.ashman.ontrack.network.services.share

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.share.Comment
import de.ashman.ontrack.domain.share.Like
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.domain.share.SimplePost
import de.ashman.ontrack.network.services.share.dto.CommentDto
import de.ashman.ontrack.network.services.share.dto.CreateCommentDto
import de.ashman.ontrack.network.services.share.dto.LikeDto
import de.ashman.ontrack.network.services.share.dto.Page
import de.ashman.ontrack.network.services.share.dto.PostDto
import de.ashman.ontrack.network.services.share.dto.SimplePostDto
import de.ashman.ontrack.network.services.share.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface PostService {
    suspend fun getPosts(page: Int, size: Int): Result<List<SimplePost>>
    suspend fun getPost(postId: String): Result<Post>
    suspend fun toggleLike(postId: String): Result<Like>
    suspend fun addComment(postId: String, dto: CreateCommentDto): Result<Comment>
    suspend fun deleteComment(postId: String, commentId: String): Result<Unit>
    suspend fun getComments(postId: String, page: Int, size: Int): Result<List<Comment>>
    suspend fun getLikes(postId: String, page: Int, size: Int): Result<List<Like>>
}

class PostServiceImpl(
    private val httpClient: HttpClient,
) : PostService {

    override suspend fun getPosts(page: Int, size: Int): Result<List<SimplePost>> = safeApiCall {
        httpClient.get("/posts") {
            parameter("page", page)
            parameter("size", size)
        }.body<Page<SimplePostDto>>().content.map { it.toDomain() }
    }

    override suspend fun getPost(postId: String): Result<Post> = safeApiCall {
        httpClient.get("/posts/$postId").body<PostDto>().toDomain()
    }

    override suspend fun toggleLike(postId: String): Result<Like> = safeApiCall {
        httpClient.post("/posts/$postId/like").body<LikeDto>().toDomain()
    }

    override suspend fun addComment(postId: String, dto: CreateCommentDto): Result<Comment> = safeApiCall {
        httpClient.post("/posts/$postId/comment") {
            setBody(dto)
        }.body<CommentDto>().toDomain()
    }

    override suspend fun deleteComment(postId: String, commentId: String): Result<Unit> = safeApiCall {
        httpClient.delete("/posts/$postId/comment/$commentId")
    }

    override suspend fun getComments(postId: String, page: Int, size: Int): Result<List<Comment>> = safeApiCall {
        httpClient.get("/posts/$postId/comments") {
            parameter("page", page)
            parameter("size", size)
        }.body<Page<CommentDto>>().content.map { it.toDomain() }
    }

    override suspend fun getLikes(postId: String, page: Int, size: Int): Result<List<Like>> = safeApiCall {
        httpClient.get("/posts/$postId/likes") {
            parameter("page", page)
            parameter("size", size)
        }.body<Page<LikeDto>>().content.map { it.toDomain() }
    }
}
