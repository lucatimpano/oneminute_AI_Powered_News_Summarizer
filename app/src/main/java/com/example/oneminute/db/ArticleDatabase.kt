package com.example.oneminute.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.oneminute.models.Article

//questa classe rappresenta il database principale

@Database(
    entities = [Article::class], //la tabella si basa sull'entità article
    version=1

)

@TypeConverters(Converters::class)//registriamo i convertitori della classe Converters


abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDAO
    companion object{
        @Volatile
        private var instance: ArticleDatabase?=null
        private val LOCK=Any()

        operator  fun invoke(context:Context)= instance?: synchronized(LOCK)
        //synchronized(LOCK) previene la creazione di più istanze in caso di accessi concorrenti
        {
            //se l'istanza non esiste (quindi il database) allora viene creata con createDatabase(context)
            instance ?: createDatabase(context).also{
                instance=it

            }
        }

        private fun createDatabase(context: Context)=

            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()


        }


    }