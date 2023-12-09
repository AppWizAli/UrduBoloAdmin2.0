package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson

data class Admin @JvmOverloads constructor(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var docId: String = "",
    var role: String = "",
    var favorites: String = "",
    var profile: String = "",
    var loggedInNumber: String = "",
    var location: String = "",
    val createdAt: Timestamp = Timestamp.now() // Creation timestamp

)
{

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): Admin? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, Admin::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}