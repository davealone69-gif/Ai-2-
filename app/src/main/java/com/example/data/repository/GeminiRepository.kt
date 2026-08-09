package com.example.data.repository

import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository enforcing enterprise Gemini API Client integration & Safety Overrides.
 */
class GeminiRepository(
    private val defaultModelName: String = "gemini-1.5-flash"
) {

    /**
     * Programmatically constructs GenerativeModel with Safety Overrides (BlockThreshold.NONE).
     */
    private fun createGenerativeModel(
        modelName: String = defaultModelName,
        systemInstruction: String? = null,
        temperature: Float = 0.9f
    ): GenerativeModel {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE)
        )

        val config = generationConfig {
            this.temperature = temperature
            this.topP = 0.95f
            this.topK = 40
        }

        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            safetySettings = safetySettings,
            generationConfig = config,
            systemInstruction = systemInstruction?.let { content { text(it) } }
        )
    }

    /**
     * Executes prompt generation wrapped safely in [runCatching].
     */
    suspend fun generateContent(
        prompt: String,
        modelName: String = defaultModelName,
        systemInstruction: String? = null,
        temperature: Float = 0.9f
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalArgumentException("Gemini API key is missing. Please set GEMINI_API_KEY in the Secrets panel.")
            )
        }

        runCatching {
            val model = createGenerativeModel(modelName, systemInstruction, temperature)
            val response = model.generateContent(prompt)
            response.text ?: throw IllegalStateException("Gemini returned an empty response candidate.")
        }
    }
}
