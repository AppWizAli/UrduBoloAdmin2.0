package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.Collections.emptyList
import java.util.Collections.emptyMap
import java.util.Date


data class ModelVideo @JvmOverloads constructor(
    var docId: String = "",
    var title: String = "default",
    var subtitle: String = "",
    var description: String = "",
    var seasonId: String = "",
    var dramaId: String = "",
    var dramaName: String = "",
    var episodeno: String = "",
    var totalepisodes: String = "",
    var videoId: String = "",
    var privacy: String = "",
    var thumbnail: String = "",
    var videourl: String = "",
    var isDownload: String = "",
    var downloadType: String = "",
    var comments: List<ModelComment> = emptyList(),
    var like: String = "0",
    var dislike: String = "0",
    val uploadedAt: Date = Date(), // Creation timestamp
    var category: String = "", // Category of the video (e.g., Comedy, Drama, Music)
    var duration: String = "", // Duration of the video in HH:MM:SS format
    var resolution: String = "", // Video resolution (e.g., 1920x1080)
    var location: String = "", // Geographical location of the video recording
    var views: String = "0", // Number of views
    var favorites: String = "0",
    var users_Id: List<String> = emptyList()
)
{

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelVideo? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelVideo::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
class ModelComment {
    var commentId: String = "" // Unique identifier for the comment
    var userId: String = "" // User ID of the commenter
    var userName: String = "" // User name of the commenter
    var commentText: String = "" // Text of the comment
    var timestamp: Timestamp=Timestamp.now() // Timestamp indicating when the comment was posted

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelComment? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelComment::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}
