package gr.aueb.thriveon.network.api

import gr.aueb.thriveon.BuildConfig
import gr.aueb.thriveon.network.model.ChatRequest
import gr.aueb.thriveon.network.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer ${BuildConfig.OPENAI_API_KEY}"
    )
    @POST("chat/completions")
    suspend fun getChatCompletion(@Body request: ChatRequest): ChatResponse
}
