package com.example.oneminute.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.oneminute.models.Article

//questa classe rappresenta il database principale

@Database(
    entities = [Article::class], //la tabella si basa sull'entità article
    version=2
)



@TypeConverters(Converters::class)//registriamo i convertitori della classe Converters


abstract class ArticleDatabase: RoomDatabase() {

    abstract fun getArticleDao(): ArticleDAO
    companion object{
        @Volatile
        private var instance: ArticleDatabase?=null
        private val LOCK=Any()
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Controlla se la colonna esiste ed è configurata correttamente
                database.execSQL("ALTER TABLE articles RENAME TO articles_temp")
                database.execSQL(
                    """
            CREATE TABLE articles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                author TEXT NOT NULL DEFAULT 'Unknown Author',
                content TEXT NOT NULL DEFAULT 'No Content',
                description TEXT NOT NULL DEFAULT 'No Description',
                publishedAt TEXT NOT NULL DEFAULT 'Unknown Date',
                source TEXT DEFAULT NULL, -- Modifica per rendere nullable
                title TEXT NOT NULL DEFAULT 'Untitled',
                url TEXT NOT NULL DEFAULT 'No URL',
                urlToImage TEXT NOT NULL DEFAULT 'No Image URL'
            )
            """
                )
                database.execSQL(
                    """
            INSERT INTO articles (id, author, content, description, publishedAt, source, title, url, urlToImage)
            SELECT id, author, content, description, publishedAt, source, title, url, urlToImage
            FROM articles_temp
            """
                )
                database.execSQL("DROP TABLE articles_temp")
            }
        }

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
            ).addMigrations(MIGRATION_1_2).build()
        }
    }