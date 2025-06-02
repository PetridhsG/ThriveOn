package gr.aueb.thriveon.network.api

import gr.aueb.thriveon.BuildConfig
import gr.aueb.thriveon.network.model.ChatMessage
import gr.aueb.thriveon.network.model.ChatRequest
import gr.aueb.thriveon.network.model.UserRequest
import gr.aueb.thriveon.network.model.UserResponse
import kotlinx.serialization.json.Json

interface ApiClient {
    suspend fun getUserPreferences(request: UserRequest): UserResponse
}

class ApiClientImpl(private val apiService: ApiService) : ApiClient {

    override suspend fun getUserPreferences(request: UserRequest): UserResponse {
        val prompt = createPromptFromRequest(request)
        val chatRequest = buildChatRequest(prompt)

        var lastError: Exception? = null

        repeat(10) { attempt ->
            try {
                val response = apiService.getChatCompletion(chatRequest)
                val responseContent = response.choices.firstOrNull()?.message?.content

                println("🔍 LLM Response (attempt ${attempt + 1}): $responseContent")

                if (responseContent != null && responseContent.trim().startsWith("{") && responseContent.contains("suggested_task_ids")) {
                    return parseJsonResponse(responseContent)
                } else {
                    println("⚠️ Malformed LLM response, will retry...")
                    lastError = IllegalStateException("Invalid LLM response content")
                }
            } catch (e: Exception) {
                lastError = e
            }
        }

        throw lastError ?: IllegalStateException("LLM failed with unknown error")
    }


    private fun buildChatRequest(prompt: String): ChatRequest {
        return ChatRequest(
            model = BuildConfig.MODEL,
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "You are a helpful task recommendation assistant."
                ),
                ChatMessage(role = "user", content = prompt)
            ),
            temperature = BuildConfig.TEMPERATURE,
            max_tokens = 512
        )
    }

    private fun parseJsonResponse(content: String): UserResponse {
        try {
            if (!content.contains("suggested_task_ids")) {
                throw IllegalStateException("Malformed response: missing 'suggested_task_ids'.")
            }
            return json.decodeFromString(content)
        } catch (e: Exception) {
            println("❌ JSON parse failed: ${e.message}")
            throw e
        }
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }
    }

    private fun createPromptFromRequest(request: UserRequest): String = buildString {
        appendLine("You are a helpful assistant that ONLY returns JSON with task suggestions.")
        appendLine("DO NOT explain anything. DO NOT include any prose or markdown.")
        appendLine("Return ONLY the JSON object exactly as shown below—no extra text or comments.")

        appendLine()
        appendLine("🎯 Your goal: Suggest exactly 3 task IDs for the user by considering:")
        appendLine("- User preferences and interests")
        appendLine("- Category engagement (progress)")
        appendLine("- Completed tasks and their ratings (scale 1-5)")
        appendLine("- Excluding active and currently suggested tasks")
        appendLine("- Picking ONLY from the available list of tasks provided")
        appendLine("- Avoiding tasks recently suggested but not chosen — unless highly appropriate")

        appendLine()
        appendLine("⚖️ Strategy:")
        appendLine("- Prefer categories with higher engagement, but include some variety.")
        appendLine("- Include at least one task from the category that the user are most engaged to.")
        appendLine("- Include at least one task from a less-developed or related category to promote discovery.")
        appendLine("- Avoid repeating the same task combinations across sessions.")
        appendLine("- Ensure diversity across multiple calls even if the input is similar.")

        appendLine()
        appendLine("🧠 You may infer similarities between preferences and categories when helpful.")
        appendLine("Use smart judgment to balance relevance and novelty.")

        appendLine()
        appendLine("## Preferences")
        appendLine("User preferences: ${request.preferences}")

        appendLine()
        appendLine("## Category Progress")
        appendLine("Category progress (more value = more activity): ${request.categoryProgress}")

        appendLine()
        appendLine("## Completed Tasks with Ratings")
        if (request.completedTasks.isEmpty()) {
            appendLine("No completed tasks.")
        } else {
            request.completedTasks.forEach {
                appendLine("- ${it.title} (ID: ${it.id}, Rating: ${it.rating})")
            }
        }

        appendLine()
        appendLine("## Active Task IDs (exclude from suggestions)")
        appendLine(request.activeTasks)

        appendLine()
        appendLine("## Current Suggestions (exclude from suggestions)")
        appendLine(request.currentSuggestions)

        appendLine()
        appendLine("## Previously Suggested but Not Selected")
        appendLine("The following task IDs were shown to the user in the past but not selected or completed:")
        appendLine(request.suggestionHistory)
        appendLine("Avoid repeating these unless they are highly relevant or very well aligned with preferences.")

        appendLine()
        appendLine("## Available Tasks to Choose From (select exactly 3 from this list)")
        request.allTasks.forEach {
            appendLine("- ID: ${it.id}, Title: ${it.title}, Category: ${it.categoryTitle}")
        }

        appendLine()
        appendLine("🆕 Session ID: ${System.currentTimeMillis()}")

        appendLine()
        appendLine("📦 Respond ONLY with a valid JSON object using the EXACT format below:")
        appendLine(
            """
        {
          "suggested_task_ids": ["id_1", "id_2", "id_3"]
        }
        """.trimIndent()
        )
    }
}
