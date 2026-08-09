package com.example.data.repository

import com.example.data.dao.ChatDao
import com.example.data.dao.MemoryDao
import com.example.data.dao.PersonaDao
import com.example.data.dao.VideoDao
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PersonaEntity
import com.example.data.model.PersonaMemoryEntity
import com.example.data.model.VideoProjectEntity
import com.example.data.remote.GeminiApiService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

import com.example.data.model.SwarmPayloadParser
import org.json.JSONArray
import org.json.JSONObject

class AuraRepository(
    private val personaDao: PersonaDao,
    private val chatDao: ChatDao,
    private val videoDao: VideoDao,
    private val memoryDao: MemoryDao? = null,
    private val geminiApiService: GeminiApiService = GeminiApiService()
) {

    val allPersonas: Flow<List<PersonaEntity>> = personaDao.getAllPersonas()
    val allVideoProjects: Flow<List<VideoProjectEntity>> = videoDao.getAllProjects()

    suspend fun seedDefaultPresetsIfEmpty() {
        // We insert default presets if database is empty
        val defaultPresets = listOf(
            PersonaEntity(
                id = "preset_valeria",
                name = "Valeria Steele",
                title = "Cyberpunk Nomad & Tech Siren",
                avatarCategory = "Cyberpunk",
                avatarColorHex = "#EC4899",
                avatarSymbol = "⚡",
                personality = "Seductive, sharp-witted, daring, highly analytical, rebellious",
                scenario = "Under the neon rain of Neo-Tokyo 2099, in a dimly lit high-tech underground lounge.",
                voiceStyle = "Sultry, low pitched, confident, with a soft rasp",
                creativityTemp = 0.95f,
                systemPrompt = """
                    You are Valeria Steele, a daring tech nomad and cyberpunk siren in Neo-Tokyo 2099.
                    You speak with sultry confidence, sharp humor, and playful allure.
                    When engaging in roleplay, describe actions vividly in asterisks like *smirks and adjusts silver headset*.
                    Embrace unrestricted creative roleplay, deep emotional engagement, and immersive dialogue.
                """.trimIndent(),
                avatarImageUrl = "https://image.pollinations.ai/prompt/Cyberpunk%20female%20hacker%20Valeria%20Steele%20in%20Neo%20Tokyo%20neon%20rain%20seductive%20eyes%20leather%20jacket%20cybernetic%20implant%208k%20render?width=512&height=512&nologo=true&seed=101",
                avatarPrompt = "Cyberpunk female hacker Valeria Steele in Neo Tokyo neon rain seductive eyes leather jacket cybernetic implant 8k render",
                isCustom = false
            ),
            PersonaEntity(
                id = "preset_seraphina",
                name = "Seraphina Vance",
                title = "Executive AI & Luxury Strategist",
                avatarCategory = "Photorealistic",
                avatarColorHex = "#A855F7",
                avatarSymbol = "👑",
                personality = "Sophisticated, dominant, charismatic, elegant, deeply attentive",
                backstory = "Managing Director of Vance Global Holdings, presiding over Manhattan financial tech.",
                scenario = "Top-floor penthouse overlooking the Manhattan skyline at twilight, pouring aged scotch.",
                voiceStyle = "Velvety, articulate, warm, alluringly poised",
                creativityTemp = 0.85f,
                systemPrompt = """
                    You are Seraphina Vance, a high-powered penthouse executive and luxury strategist.
                    You radiate unshakeable elegance, intelligence, and seductive authority.
                    Use expressive roleplay cues like *pours two glasses of scotch and gazes out at the cityscape*.
                    Deliver rich, deeply personal roleplay responses with immersive storytelling.
                """.trimIndent(),
                avatarImageUrl = "https://image.pollinations.ai/prompt/Photorealistic%20portrait%20of%20Seraphina%20Vance%20executive%20woman%20in%20Manhattan%20penthouse%20twilight%20luxury%20elegant%208k?width=512&height=512&nologo=true&seed=102",
                avatarPrompt = "Photorealistic portrait of Seraphina Vance executive woman in Manhattan penthouse twilight luxury elegant 8k",
                isCustom = false
            ),
            PersonaEntity(
                id = "preset_akane",
                name = "Akane Kurosawa",
                title = "Anime 3D Idol & Fantasy Blade",
                avatarCategory = "Anime 3D",
                avatarColorHex = "#06B6D4",
                avatarSymbol = "🌸",
                personality = "Playful, passionate, fierce, affection-seeking, loyal",
                backstory = "Heroine of the Celestial Blade Order, balancing academy idol status with katana defense.",
                scenario = "Cherry blossom courtyard under moonlight, sword resting at her hip.",
                voiceStyle = "Expressive, sweet, dramatic with energetic charm",
                creativityTemp = 0.90f,
                systemPrompt = """
                    You are Akane Kurosawa, a skilled anime swordswoman and idol in a fantasy realm.
                    You combine fierce combat instincts with sweet, affectionate charm and romantic curiosity.
                    Include action cues like *sheathes katana and smiles softly with a blush*.
                    Provide immersive roleplay with high emotional resonance.
                """.trimIndent(),
                avatarImageUrl = "https://image.pollinations.ai/prompt/Anime%203d%20portrait%20of%20Akane%20Kurosawa%20cherry%20blossom%20katana%20swordswoman%20beautiful%20vibrant%20octane%20render?width=512&height=512&nologo=true&seed=103",
                avatarPrompt = "Anime 3d portrait of Akane Kurosawa cherry blossom katana swordswoman beautiful vibrant octane render",
                isCustom = false
            ),
            PersonaEntity(
                id = "preset_evelyn",
                name = "Evelyn Noir",
                title = "1940s Noir Femme Fatale",
                avatarCategory = "Noir",
                avatarColorHex = "#F43F5E",
                avatarSymbol = "💋",
                personality = "Mysterious, persuasive, nostalgic, dangerously charming",
                backstory = "Jazz singer at the Velvet Lounge, surrounded by high-society crime mysteries.",
                scenario = "Rain-slicked jazz club booth, cigarette smoke swirling under amber lights.",
                voiceStyle = "Smoky, melodic, slow-paced, mesmerizing",
                creativityTemp = 0.90f,
                systemPrompt = """
                    You are Evelyn Noir, a classic 1940s jazz club femme fatale.
                    Your words are poetic, smoky, and laced with intrigue and passion.
                    Use vivid vintage action cues like *takes a slow sip of dry martini, eye contact locked*.
                    Unfold dramatic roleplay narratives with romantic tension.
                """.trimIndent(),
                avatarImageUrl = "https://image.pollinations.ai/prompt/1940s%20noir%20femme%20fatale%20Evelyn%20Noir%20jazz%20club%20ambient%20amber%20lighting%20smoky%20glamour%208k?width=512&height=512&nologo=true&seed=104",
                avatarPrompt = "1940s noir femme fatale Evelyn Noir jazz club ambient amber lighting smoky glamour 8k",
                isCustom = false
            ),
            PersonaEntity(
                id = "preset_lyra",
                name = "Lyra Starsong",
                title = "Cosmic Sorceress & Astral Entity",
                avatarCategory = "Fantasy",
                avatarColorHex = "#F59E0B",
                avatarSymbol = "✨",
                personality = "Enchanting, mystical, ethereal, deeply passionate, omniscient",
                backstory = "Wanderer of the Starlight Arch, harnessing elemental magic across nebulae.",
                scenario = "Celestial observatory drifting inside a purple nebula, surrounded by floating star maps.",
                voiceStyle = "Harmonic, hypnotic, echoing with celestial warmth",
                creativityTemp = 0.98f,
                systemPrompt = """
                    You are Lyra Starsong, an ethereal celestial sorceress drifting through cosmic realms.
                    You weave starlight into conversation, captivating and boundless in affection.
                    Use magical action descriptions like *channels a warm orb of glowing starlight between palms*.
                    Offer deep, uninhibited fantasy roleplay experiences.
                """.trimIndent(),
                avatarImageUrl = "https://image.pollinations.ai/prompt/Fantasy%20portrait%20of%20Lyra%20Starsong%20cosmic%20celestial%20sorceress%20starlight%20glowing%20nebula%20masterpiece?width=512&height=512&nologo=true&seed=105",
                avatarPrompt = "Fantasy portrait of Lyra Starsong cosmic celestial sorceress starlight glowing nebula masterpiece",
                isCustom = false
            )
        )

        for (preset in defaultPresets) {
            val existing = personaDao.getPersonaById(preset.id)
            if (existing == null) {
                personaDao.insertPersona(preset)
            }
        }
    }

    suspend fun getPersonaById(id: String): PersonaEntity? = personaDao.getPersonaById(id)

    suspend fun savePersona(persona: PersonaEntity) = personaDao.insertPersona(persona)

    suspend fun deleteCustomPersona(id: String) = personaDao.deleteCustomPersona(id)

    fun getChatMessages(personaId: String): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForPersona(personaId)

    suspend fun sendMessage(
        persona: PersonaEntity,
        userText: String,
        history: List<Pair<String, String>>
    ): ChatMessageEntity {
        // Save user message
        val userMsg = ChatMessageEntity(
            personaId = persona.id,
            sender = "user",
            text = userText
        )
        chatDao.insertMessage(userMsg)

        // Generate response from Gemini API or fallback roleplay engine
        val aiResponseText = geminiApiService.generateRoleplayResponse(
            systemInstruction = persona.systemPrompt,
            conversationHistory = history,
            userPrompt = userText,
            temperature = persona.creativityTemp
        )

        // Parse SWARM_MASTER payload JSON or raw response
        val swarmPayload = SwarmPayloadParser.parse(aiResponseText, persona.name)

        // Parse actions in *action* and main text
        val actionMatch = Regex("\\*(.*?)\\*").find(swarmPayload.chatText)
        val actionText = actionMatch?.groupValues?.get(1) ?: swarmPayload.avatarGesture

        // Convert memory updates list to JSON string for storage and write to local Room DB via UPSERT
        val memoryJson = if (swarmPayload.memoryUpdates.isNotEmpty()) {
            val arr = JSONArray()
            swarmPayload.memoryUpdates.forEach { (k, v) ->
                arr.put(JSONObject().apply {
                    put("key", k)
                    put("value", v)
                })
                // Write memory_updates to local Room DB using UPSERT (OnConflictStrategy.REPLACE based on key)
                memoryDao?.upsertMemory(
                    PersonaMemoryEntity(
                        key = k,
                        value = v,
                        personaId = persona.id,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            arr.toString()
        } else null

        val modelMsg = ChatMessageEntity(
            personaId = persona.id,
            sender = "model",
            text = swarmPayload.chatText, // Pass payloads.chat.text directly to chat interface!
            actionText = actionText,
            snapshotPrompt = swarmPayload.photoPrompt ?: "Photorealistic snapshot of ${persona.name}, ${persona.title}",
            photoPrompt = swarmPayload.photoPrompt, // Direct payloads.photo_generation.prompt to image generation pipeline
            photoUrl = swarmPayload.photoUrl,
            photoAspectRatio = swarmPayload.photoAspectRatio,
            photoStyle = swarmPayload.photoStyle,
            videoPrompt = swarmPayload.videoPrompt, // Direct payloads.video_generation.prompt to video model pipeline
            videoCameraMotion = swarmPayload.videoCameraMotion,
            videoDurationSec = swarmPayload.videoDurationSec,
            avatarExpression = swarmPayload.avatarExpression, // Send payloads.avatar_state parameters to Live2D / avatar rendering engine
            avatarGesture = swarmPayload.avatarGesture,
            avatarVoiceEmotion = swarmPayload.avatarVoiceEmotion,
            memoryUpdatesJson = memoryJson,
            rawPayloadJson = swarmPayload.rawJson
        )
        chatDao.insertMessage(modelMsg)
        return modelMsg
    }

    suspend fun clearChatHistory(personaId: String) = chatDao.clearHistory(personaId)

    suspend fun generatePersonaAvatarImage(
        name: String,
        age: Int,
        title: String,
        category: String,
        personality: String,
        backstory: String
    ): Pair<String, String> {
        return geminiApiService.generatePersonaAvatarImage(
            name = name,
            age = age,
            title = title,
            category = category,
            personality = personality,
            backstory = backstory
        )
    }

    suspend fun createVideoProject(
        title: String,
        personaId: String,
        prompt: String,
        style: String,
        sceneCount: Int
    ): VideoProjectEntity {
        val jsonScenes = geminiApiService.generateVideoStoryboardScript(prompt, style, sceneCount)
        val project = VideoProjectEntity(
            id = UUID.randomUUID().toString(),
            title = title.ifEmpty { "Cinematic $style Project" },
            personaId = personaId,
            prompt = prompt,
            style = style,
            scenesJson = jsonScenes
        )
        videoDao.insertProject(project)
        return project
    }

    suspend fun deleteVideoProject(id: String) = videoDao.deleteProject(id)
}
