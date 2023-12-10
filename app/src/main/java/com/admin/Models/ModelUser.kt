package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.Date


data class ModelUser @JvmOverloads constructor(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var userId: String = "",
    var videoId: String = "",
    var favorites: String = "",
    var profile: String = "",
    var loggedInNumber: String = "",
    var location: String = "",
    var status: String = "",
    val createdAt: Timestamp = Timestamp.now() // Creation timestamp

)
{

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelUser? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelUser::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}