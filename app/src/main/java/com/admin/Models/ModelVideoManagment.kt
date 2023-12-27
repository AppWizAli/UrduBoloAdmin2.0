package com.admin.Models

import com.google.gson.Gson

class ModelVideoManagment
    (
    var video_Id:String="",
    var group_Id:String="" ,
    var seasonId:String="",
    var doc_Id:String=""
            )
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