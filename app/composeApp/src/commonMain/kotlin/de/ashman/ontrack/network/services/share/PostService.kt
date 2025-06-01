package de.ashman.ontrack.network.services.share

import de.ashman.ontrack.api.utils.safeApiCall
import de.ashman.ontrack.domain.share.Comments
import de.ashman.ontrack.domain.share.Likes
import de.ashman.ontrack.domain.share.Post
import de.ashman.ontrack.network.services.share.dto.CommentsDto
import de.ashman.ontrack.network.services.share.dto.CreateCommentDto
import de.ashman.ontrack.network.services.share.dto.LikesDto
import de.ashman.ontrack.network.services.share.dto.Page
import de.ashman.ontrack.network.services.share.dto.PostDto
import de.ashman.ontrack.network.services.share.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

interface PostService {
    suspend fun getPosts(page: Int, size: Int): Result<List<Post>>
    suspend fun getPost(postId: String): Result<Post>
    suspend fun toggleLike(postId: String): Result<Post>
    suspend fun addComment(postId: String, dto: CreateCommentDto): Result<Post>
    suspend fun deleteComment(postId: String, commentId: String): Result<Post>
    suspend fun getComments(postId: String, page: Int, size: Int): Result<Comments>
    suspend fun getLikes(postId: String, page: Int, size: Int): Result<Likes>
}

class PostServiceImpl(
    private val httpClient: HttpClient,
) : PostService {

    override suspend fun getPosts(page: Int, size: Int): Result<List<Post>> = safeApiCall {
        httpClient.get("/posts") {
            parameter("page", page)
            parameter("size", size)
        }.body<Page<PostDto>>().content.map { it.toDomain() }
    }

    override suspend fun getPost(postId: String): Result<Post> = safeApiCall {
        httpClient.get("/posts/$postId").body<PostDto>().toDomain()
    }

    override suspend fun toggleLike(postId: String): Result<Post> = safeApiCall {
        httpClient.post("/posts/$postId/like")
            .body<PostDto>()
            .toDomain()
    }

    override suspend fun addComment(postId: String, dto: CreateCommentDto): Result<Post> = safeApiCall {
        httpClient.post("/posts/$postId/comment") {
            setBody(dto)
        }.body<PostDto>().toDomain()
    }

    override suspend fun deleteComment(postId: String, commentId: String): Result<Post> = safeApiCall {
        httpClient.delete("/posts/$postId/comment/$commentId").body<PostDto>().toDomain()
    }

    override suspend fun getComments(postId: String, page: Int, size: Int): Result<Comments> = safeApiCall {
        httpClient.get("/posts/$postId/comments") {
            parameter("page", page)
            parameter("size", size)
        }.body<CommentsDto>().toDomain()
    }

    override suspend fun getLikes(postId: String, page: Int, size: Int): Result<Likes> = safeApiCall {
        httpClient.get("/posts/$postId/likes") {
            parameter("page", page)
            parameter("size", size)
        }.body<LikesDto>().toDomain()
    }
}
