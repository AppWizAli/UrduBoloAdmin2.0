package com.admin.Data

import android.content.Context
import android.content.SharedPreferences
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class SharedPrefManager(var context: Context) {


    private val sharedPref: SharedPreferences = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()



    fun getDramaList(): List<ModelDrama>{

        val json = sharedPref.getString("ListDrama", "") ?: ""
        val type: Type = object : TypeToken<List<ModelDrama?>?>() {}.getType()
        return Gson().fromJson(json, type)
    }

    fun putDramaList(list: List<ModelDrama>) {
        editor.putString("ListDrama", Gson().toJson(list))
        editor.commit()
    }

    fun putUserList(list: List<ModelUser>) {
        editor.putString("ListUsers", Gson().toJson(list))
        editor.commit()
    }
    fun getUserList(): List<ModelUser> {
        val json = sharedPref.getString("ListUsers", "") ?: ""
        val type: Type = object : TypeToken<List<ModelUser?>?>() {}.type

        return Gson().fromJson(json, type) ?: emptyList()
    }
    fun getSeasonList(): List<ModelSeason> {
        val json = sharedPref.getString("ListSeasons", "") ?: ""
        val type: Type = object : TypeToken<List<ModelSeason?>?>() {}.type

        return Gson().fromJson(json, type) ?: emptyList()
    }


    fun putSeasonList(list: List<ModelSeason>) {
        editor.putString("ListSeasons", Gson().toJson(list))
        editor.commit()
    }
    fun getVideoList(): ArrayList<ModelVideo> {
        val json = sharedPref.getString("ListVideo", "") ?: ""
        val type: Type = object : TypeToken<ArrayList<ModelVideo>>() {}.type

        return Gson().fromJson(json, type) ?: ArrayList()
    }



    fun putVideoList(list: ArrayList<ModelVideo>) {
        editor.putString("ListVideo", Gson().toJson(list))
        editor.commit()
    }

}