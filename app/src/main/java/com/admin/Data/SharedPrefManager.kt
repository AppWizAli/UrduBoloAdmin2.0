package com.admin.Data

import android.content.Context
import android.content.SharedPreferences
import com.admin.Models.Admin
import com.admin.Models.ModelDrama
import com.admin.Models.ModelSeason
import com.admin.Models.ModelUser
import com.admin.Models.ModelVideo
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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


    public  fun saveAdminLogin(isLoggedIn:Boolean)
    {
        editor.putBoolean("isLoggedIn",isLoggedIn)
        editor.commit()
    }
    public  fun SaveCelebration(isLoggedIn:Boolean) {
        editor.putBoolean("IsCelebration", isLoggedIn)
        editor.commit()
    }
        fun saveAdmin(user: Admin) {

            editor.putString("admin", Gson().toJson(user))
            editor.commit()


        }

    fun getAdmin(): Admin {
        val json = sharedPref.getString("admin", "") ?: ""

        // If the JSON string is empty or null, return a default InvestmentModel object
        if (json.isEmpty()) {
            return Admin() // Replace this with your default InvestmentModel constructor
        }

        // Try to deserialize the JSON string into an InvestmentModel object
        return try {
            Gson().fromJson(json, Admin::class.java)
        } catch (e: JsonSyntaxException) {
            // If the deserialization fails, return a default InvestmentModel object
            Admin() // Replace this with your default InvestmentModel constructor
        }
    }



    public  fun isLoggedIn():Boolean
    {
      return  sharedPref.getBoolean("isLoggedIn",false)
    }
    public  fun isCeleBration():Boolean
    {
      return  sharedPref.getBoolean("IsCelebration",false)!!
    }

}