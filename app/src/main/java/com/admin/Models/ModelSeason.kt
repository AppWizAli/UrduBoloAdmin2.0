package com.admin.Models

import com.google.firebase.Timestamp
import com.google.gson.Gson

class ModelSeason(
var seasonNo: String = "",
var docId: String = "",
var dramaId: String = "",
var totalEpisode: String = "",
var uploadedepisodes: String = "",
var thumbnail: String = "",
var dramathumbnail: String = "",
var subtitle: String = "",
var uploadedAt: Timestamp = Timestamp.now()
){
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelSeason? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelSeason::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}