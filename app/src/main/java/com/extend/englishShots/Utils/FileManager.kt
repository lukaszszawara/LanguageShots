package com.extend.englishShots.Utils
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class FileManager {
    private fun getJsonString(context: Context): String? {
       return getJsonDataFromAsset(context, "dictionary.json");
    }
    fun getObjectFromString(context:Context?): HashMap<String,String>{
        val gson = Gson()
        val listPersonType = object : TypeToken<HashMap<String,String>>() {}.type
        val jsonFileString = context?.let { getJsonString(it) }
        val dictionary: HashMap<String,String> = gson.fromJson(jsonFileString, listPersonType)
        return dictionary
    }


    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

}