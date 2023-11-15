package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson

class ModelDrama(
    var docId: String = "",
    var dramaName: String = "",
    var totalSeason: String = "",
    var dramaNumber: String = "",
    var thumbnail: String = "",
    var title: String = "",
    var subtitle: String = "",
    var uploadedAt:Timestamp=Timestamp.now()
) {
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelDrama? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelDrama::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}