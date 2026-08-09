package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateRoleplayResponse(
        systemInstruction: String,
        conversationHistory: List<Pair<String, String>>, // Pair(sender, text)
        userPrompt: String,
        temperature: Float = 0.9f
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext simulateOfflineRoleplayResponse(userPrompt, systemInstruction)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System instruction
            val sysInstructionObj = JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
            }
            rootJson.put("systemInstruction", sysInstructionObj)

            // Generation config
            val genConfigObj = JSONObject().apply {
                put("temperature", temperature.toDouble())
                put("topP", 0.95)
                put("topK", 40)
            }
            rootJson.put("generationConfig", genConfigObj)

            // Contents array
            val contentsArray = JSONArray()
            for ((sender, text) in conversationHistory.takeLast(10)) {
                val role = if (sender == "user") "user" else "model"
                val contentObj = JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().put(JSONObject().put("text", text)))
                }
                contentsArray.put(contentObj)
            }

            // Latest user prompt
            val latestUserObj = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
            }
            contentsArray.put(latestUserObj)

            rootJson.put("contents", contentsArray)

            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string()
                    return@withContext simulateOfflineRoleplayResponse(userPrompt, systemInstruction)
                }

                val respStr = response.body?.string() ?: ""
                val respJson = JSONObject(respStr)
                val candidates = respJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val cand = candidates.getJSONObject(0)
                    val content = cand.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "")
                    }
                }
                return@withContext simulateOfflineRoleplayResponse(userPrompt, systemInstruction)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext simulateOfflineRoleplayResponse(userPrompt, systemInstruction)
        }
    }

    suspend fun generateVideoStoryboardScript(
        prompt: String,
        style: String,
        sceneCount: Int = 4
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val systemInstruction = """
            You are an expert AI Video Director & Cinematic Storyboard Creator.
            Generate a detailed multi-scene video script for the topic '$prompt' in '$style' style with exactly $sceneCount scenes.
            For each scene provide:
            1. Shot Type (e.g., Extreme Close-Up, Cinematic Wide Shot, Tracking Shot)
            2. Visual Description (detailed visual prompt describing lighting, mood, character pose)
            3. Narration / Dialogue Text
            4. Camera Motion (e.g., Slow Pan Right, Orbit 360, Dolly In)
            5. Audio / Music Mood (e.g., Dark Synthwave, Ethereal Ambient, Cyberpunk Bass)
            Make it atmospheric, highly engaging, and immersive.
        """.trimIndent()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext simulateOfflineStoryboardJson(prompt, style, sceneCount)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val rootJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                })
                put("contents", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", "Generate video storyboard scenes for: $prompt")))
                }))
            }

            val request = Request.Builder()
                .url(url)
                .post(rootJson.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respStr = response.body?.string() ?: ""
                    val respJson = JSONObject(respStr)
                    val text = respJson.optJSONArray("candidates")
                        ?.optJSONObject(0)
                        ?.optJSONObject("content")
                        ?.optJSONArray("parts")
                        ?.optJSONObject(0)
                        ?.optString("text", "") ?: ""
                    if (text.isNotEmpty()) return@withContext text
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext simulateOfflineStoryboardJson(prompt, style, sceneCount)
    }

    suspend fun generatePersonaAvatarImage(
        name: String,
        age: Int,
        title: String,
        category: String,
        personality: String,
        backstory: String
    ): Pair<String, String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        var generatedPrompt = "$category style portrait of $name, $age years old, $title"
        if (personality.isNotBlank()) generatedPrompt += ", $personality"
        if (backstory.isNotBlank()) generatedPrompt += ", inspired by backstory: ${backstory.take(100)}"
        generatedPrompt += ", highly detailed, cinematic lighting, masterpiece, 8k resolution"

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemInstruction = """
                    You are an expert AI Character Concept Artist and Visual Prompt Engineer.
                    Given a character's name, age, title, personality, backstory, and visual category, craft a single concise, ultra-vivid 1-sentence prompt for an AI image generator to produce an astounding visual profile picture portrait of this character.
                    Do NOT include meta commentary or quotes. Output ONLY the prompt text.
                """.trimIndent()

                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                val rootJson = JSONObject().apply {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                    })
                    put("contents", JSONArray().put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().put(JSONObject().put("text", "Name: $name, Age: $age, Title: $title, Category: $category, Personality: $personality, Backstory: $backstory")))
                    }))
                }

                val request = Request.Builder()
                    .url(url)
                    .post(rootJson.toString().toRequestBody(jsonMediaType))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val respStr = response.body?.string() ?: ""
                        val respJson = JSONObject(respStr)
                        val text = respJson.optJSONArray("candidates")
                            ?.optJSONObject(0)
                            ?.optJSONObject("content")
                            ?.optJSONArray("parts")
                            ?.optJSONObject(0)
                            ?.optString("text", "")?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            generatedPrompt = text
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val seed = (System.currentTimeMillis() % 100000).toInt()
        val encodedPrompt = java.net.URLEncoder.encode(generatedPrompt, "UTF-8")
        val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=512&height=512&nologo=true&seed=$seed"

        return@withContext Pair(imageUrl, generatedPrompt)
    }

    private fun simulateOfflineRoleplayResponse(userPrompt: String, systemInstruction: String): String {
        val actions = listOf(
            "*smirks subtly, stepping closer into the neon shadows*",
            "*tilts head thoughtfully with an intriguing glance*",
            "*adjusts collar and lets out a soft, amused laugh*",
            "*gestures towards the glowing view of the city cityscape*",
            "*leans in softly, speaking in a low, mesmerizing whisper*"
        )
        val selectedAction = actions.random()
        return "$selectedAction\n\nI hear every word you say. Tell me more about what you have in mind... $userPrompt"
    }

    private fun simulateOfflineStoryboardJson(prompt: String, style: String, sceneCount: Int): String {
        val scenes = JSONArray()
        val shotTypes = listOf("Cinematic Wide Shot", "Medium Close-Up", "Extreme Close-Up", "Drone Flyover", "Low Angle Tracking Shot")
        val cameraMotions = listOf("Slow Zoom In", "Pan Right 45deg", "Orbit Around Subject", "Dolly Back", "Static Lock")
        val audioMoods = listOf("Subtle Dark Synthwave", "Ethereal Ambient Pulse", "Cinematic Orchestral Swell", "Neon Cyberpunk Beat")

        for (i in 1..sceneCount) {
            val scene = JSONObject().apply {
                put("sceneNumber", i)
                put("shotType", shotTypes.random())
                put("visualPrompt", "$style visual keyframe: High resolution render of scene $i for '$prompt', cinematic volumetric lighting, 8k render, masterpiece")
                put("narrationText", "Scene $i: In this atmosphere of '$prompt', every frame tells a story of passion and mystery.")
                put("cameraMotion", cameraMotions.random())
                put("audioMood", audioMoods.random())
            }
            scenes.put(scene)
        }
        return scenes.toString()
    }
}
