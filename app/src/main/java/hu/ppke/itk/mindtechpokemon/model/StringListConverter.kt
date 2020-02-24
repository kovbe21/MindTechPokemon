package hu.ppke.itk.mindtechpokemon.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class StringListConverter {




        @TypeConverter
        fun fromString(value: String): List<String> {
            val listType = object : TypeToken<List<String>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun fromListLisr(list: List<String>): String {
                val gson = Gson()
                return gson.toJson(list)
        }

}