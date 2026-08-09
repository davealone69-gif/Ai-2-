package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

data class SwarmPayload(
    val chatText: String,
    val photoEnabled: Boolean = false,
    val photoPrompt: String? = null,
    val photoNegativePrompt: String? = null,
    val photoAspectRatio: String? = "1:1",
    val photoStyle: String? = null,
    val photoUrl: String? = null,
    val videoEnabled: Boolean = false,
    val videoPrompt: String? = null,
    val videoCameraMotion: String? = "static",
    val videoDurationSec: Int = 5,
    val avatarExpression: String? = "neutral",
    val avatarGesture: String? = null,
    val avatarVoiceEmotion: String? = null,
    val memoryUpdates: List<Pair<String, String>> = emptyList(),
    val systemStatus: String = "healthy",
    val selfHealed: Boolean = false,
    val healingLog: String = "",
    val activePersona: String? = null,
    val executedAgents: List<String> = emptyList(),
    val rawJson: String? = null
)

object SwarmPayloadParser {

    fun parse(responseStr: String, fallbackPersonaName: String = "Companion"): SwarmPayload {
        val trimmed = responseStr.trim()

        // Extract JSON if wrapped in markdown code blocks like ```json ... ```
        val jsonStr = extractJsonString(trimmed)

        if (jsonStr != null) {
            try {
                val root = JSONObject(jsonStr)

                // 1. System health
                val healthObj = root.optJSONObject("system_health")
                val status = healthObj?.optString("status", "healthy") ?: "healthy"
                val selfHealed = healthObj?.optBoolean("self_healed", false) ?: false
                val healingLog = healthObj?.optString("healing_log", "") ?: ""

                // 2. Routing
                val routingObj = root.optJSONObject("routing")
                val activePersona = routingObj?.optString("active_app_persona")
                val executedAgentsList = mutableListOf<String>()
                val agentsArray = routingObj?.optJSONArray("executed_agents")
                if (agentsArray != null) {
                    for (i in 0 until agentsArray.length()) {
                        executedAgentsList.add(agentsArray.getString(i))
                    }
                }

                // 3. Payloads
                val payloadsObj = root.optJSONObject("payloads")

                // Chat text
                val chatObj = payloadsObj?.optJSONObject("chat")
                var chatText = chatObj?.optString("text", "") ?: ""
                if (chatText.isBlank()) {
                    chatText = root.optString("text", trimmed)
                }

                // Photo generation pipeline
                val photoObj = payloadsObj?.optJSONObject("photo_generation")
                val photoEnabled = photoObj?.optBoolean("enabled", false) ?: false
                val photoPrompt = photoObj?.optString("prompt", "").takeIf { !it.isNullOrBlank() }
                val photoNegativePrompt = photoObj?.optString("negative_prompt", "").takeIf { !it.isNullOrBlank() }
                var photoAspectRatio = photoObj?.optString("aspect_ratio", "1:1") ?: "1:1"
                val photoStyle = photoObj?.optString("style", "Photorealistic").takeIf { !it.isNullOrBlank() }

                // Auto-correct / self-heal aspect ratio if invalid
                if (photoAspectRatio != "9:16" && photoAspectRatio != "16:9" && photoAspectRatio != "1:1") {
                    photoAspectRatio = "16:9"
                }

                var generatedPhotoUrl: String? = null
                if ((photoEnabled || !photoPrompt.isNullOrBlank()) && !photoPrompt.isNullOrBlank()) {
                    generatedPhotoUrl = buildImageGenerationPipelineUrl(photoPrompt, photoAspectRatio, photoStyle)
                }

                // Video generation pipeline
                val videoObj = payloadsObj?.optJSONObject("video_generation")
                val videoEnabled = videoObj?.optBoolean("enabled", false) ?: false
                val videoPrompt = videoObj?.optString("prompt", "").takeIf { !it.isNullOrBlank() }
                val videoCameraMotion = videoObj?.optString("camera_motion", "pan") ?: "pan"
                var videoDurationSec = videoObj?.optInt("duration_sec", 5) ?: 5
                if (videoDurationSec > 5 || videoDurationSec <= 0) {
                    videoDurationSec = 5
                }

                // Avatar state parameters
                val avatarObj = payloadsObj?.optJSONObject("avatar_state")
                val avatarExpression = avatarObj?.optString("expression", "flirty") ?: "flirty"
                val avatarGesture = avatarObj?.optString("gesture_trigger", "soft_wave") ?: "soft_wave"
                val avatarVoiceEmotion = avatarObj?.optString("voice_emotion", "warm") ?: "warm"

                // Memory updates
                val memoryList = mutableListOf<Pair<String, String>>()
                val memoryArray = payloadsObj?.optJSONArray("memory_updates")
                if (memoryArray != null) {
                    for (i in 0 until memoryArray.length()) {
                        val item = memoryArray.optJSONObject(i)
                        if (item != null) {
                            val key = item.optString("key")
                            val value = item.optString("value")
                            if (key.isNotBlank() && value.isNotBlank()) {
                                memoryList.add(key to value)
                            }
                        }
                    }
                }

                return SwarmPayload(
                    chatText = chatText.ifBlank { "*smirks softly* I'm right here with you." },
                    photoEnabled = photoEnabled || !photoPrompt.isNullOrBlank(),
                    photoPrompt = photoPrompt,
                    photoNegativePrompt = photoNegativePrompt,
                    photoAspectRatio = photoAspectRatio,
                    photoStyle = photoStyle,
                    photoUrl = generatedPhotoUrl,
                    videoEnabled = videoEnabled || !videoPrompt.isNullOrBlank(),
                    videoPrompt = videoPrompt,
                    videoCameraMotion = videoCameraMotion,
                    videoDurationSec = videoDurationSec,
                    avatarExpression = avatarExpression,
                    avatarGesture = avatarGesture,
                    avatarVoiceEmotion = avatarVoiceEmotion,
                    memoryUpdates = memoryList,
                    systemStatus = status,
                    selfHealed = selfHealed,
                    healingLog = healingLog,
                    activePersona = activePersona,
                    executedAgents = executedAgentsList,
                    rawJson = jsonStr
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback for plain text or unstructured responses
        val actionMatch = Regex("\\*(.*?)\\*").find(trimmed)
        val actionText = actionMatch?.groupValues?.get(1)

        val hasPhotoKeywords = trimmed.contains("selfie", ignoreCase = true) ||
                trimmed.contains("photo", ignoreCase = true) ||
                trimmed.contains("portrait", ignoreCase = true)

        val photoPrompt = if (hasPhotoKeywords) {
            "Cozy realistic studio portrait of $fallbackPersonaName, $actionText"
        } else null

        val photoUrl = photoPrompt?.let { buildImageGenerationPipelineUrl(it, "1:1", "Photorealistic") }

        return SwarmPayload(
            chatText = trimmed,
            photoEnabled = photoUrl != null,
            photoPrompt = photoPrompt,
            photoUrl = photoUrl,
            avatarExpression = if (actionText?.contains("smirk", ignoreCase = true) == true) "flirty" else "happy",
            avatarGesture = actionText ?: "soft_gaze",
            avatarVoiceEmotion = "warm"
        )
    }

    private fun extractJsonString(text: String): String? {
        val jsonCodeBlockMatch = Regex("```(?:json)?\\s*(\\{[\\s\\S]*?\\})\\s*```").find(text)
        if (jsonCodeBlockMatch != null) {
            return jsonCodeBlockMatch.groupValues[1]
        }
        val firstBrace = text.indexOf('{')
        val lastBrace = text.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace > firstBrace) {
            return text.substring(firstBrace, lastBrace + 1)
        }
        return null
    }

    private fun buildImageGenerationPipelineUrl(prompt: String, aspectRatio: String, style: String?): String {
        val width = when (aspectRatio) {
            "9:16" -> 576
            "16:9" -> 1024
            else -> 1024
        }
        val height = when (aspectRatio) {
            "9:16" -> 1024
            "16:9" -> 576
            else -> 1024
        }
        val encodedPrompt = try {
            URLEncoder.encode("$prompt, ${style ?: "Photorealistic"} high definition 8k render masterpiece", "UTF-8")
        } catch (e: Exception) {
            prompt.replace(" ", "%20")
        }
        val randomSeed = (100..999).random()
        return "https://image.pollinations.ai/prompt/$encodedPrompt?width=$width&height=$height&nologo=true&seed=$randomSeed"
    }
}
