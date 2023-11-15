package com.admin.Models

import com.google.gson.Gson

class ModelVideoManagment
    (
    var videoId:String="",
    var userid:String="" ,
    var seasonId:String="")
{
    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    companion object {
        fun fromString(modelFA: String): ModelVideoManagment? {
            val gson = Gson()
            return try {
                gson.fromJson(modelFA, ModelVideoManagment::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

}