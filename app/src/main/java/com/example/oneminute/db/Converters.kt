package com.example.oneminute.db

import androidx.room.TypeConverter

import com.example.oneminute.models.Source

//SQL lite, usato da Room, supporta solo i dati primitivi, dunque ci servono
//dei convertitori per trasformare oggetti complessi come Source in dati primitivi.
class Converters {

    //converte source in string
    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name ?: "Nome sconosciuto"
    }

    //converte la stringa in source
    @TypeConverter
    fun toSource(name:String): Source{
        return Source(name, name)
    }
}