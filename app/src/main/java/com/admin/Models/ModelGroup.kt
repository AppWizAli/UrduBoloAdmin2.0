package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson
import java.util.Collections

class ModelGroup(
    var name: String = "",
    var description: String = "",
    var doc_Id: String = "",
    var createdAt: Timestamp = Timestamp.now(),
    var users: List<String> = emptyList()

){
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelGroup? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelGroup::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}