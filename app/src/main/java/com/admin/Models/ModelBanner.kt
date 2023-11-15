package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson


data class ModelBanner @JvmOverloads constructor(
    var name: String = "default",
    var announcemnet: String = "",
    var heading: String = "",
    var subheading: String = "",
    var videourl: String = "",
    val uploadedAt: Timestamp=Timestamp.now(), // Creation timestamp

)
{

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelBanner: String): ModelBanner? {
            val gson = Gson()
            return try {
                gson.fromJson(modelBanner, ModelBanner::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}