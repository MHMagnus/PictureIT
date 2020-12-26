package dk.nodes.template.data.models.network

import dk.nodes.template.domain.entities.PhotoItem
import retrofit2.Response
import retrofit2.http.GET

interface Api {
    @GET("posts")
    suspend fun getPosts(): Response<List<PhotoItem>>
}
