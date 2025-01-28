package com.example.oneminute.db

import androidx.room.TypeConverter

import com.example.oneminute.models.Source

//SQL lite, usato da Room, supporta solo i dati primitivi, dunque ci servono
//dei convertitori per trasformare oggetti complessi come Source in dati primitivi.
class Converters {

    //converte source in string
    @TypeConverter
    fun fromSource(source: Source?): String {
        // Trasformiamo Source in una stringa formattata
        return if (source != null) {
            "${source.id ?: "unknown_id"}|${source.name ?: "unknown_name"}"
        } else {
            "unknown_id|unknown_name"
        }
    }

    @TypeConverter
    fun toSource(data: String): Source {
        // Scomponiamo la stringa in id e name
        val parts = data.split("|")
        val id = parts.getOrNull(0) ?: "unknown_id"
        val name = parts.getOrNull(1) ?: "unknown_name"
        return Source(id = id, name = name)
    }
}