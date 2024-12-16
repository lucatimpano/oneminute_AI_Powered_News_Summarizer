package com.example.oneminute.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oneminute.models.Article


//L'interfaccia definisce le operazioni principali per accedere e gestire i dati nella tabella articles.

@Dao
interface ArticleDAO {

    //con questo metodo inseriamo un oggetto nella tabella. Se l'oggetto da inserire
    //ha la stessa chiave primaria sostituiamo il contenuto grazie alla funzione  OnConflictStrategy.REPLACE.
    //suspend significa che, la funzione, può essere eseguita in una coroutine per non bloccare il thread principale.
    @Insert(onConflict =OnConflictStrategy.REPLACE )
    suspend fun upsert(article: Article): Long

    //esegue una query, prende tutto dalla tabella articles
    @Query("SELECT * FROM articles")
    fun getAlleArticles(): LiveData<List<Article>>


    //elimina un oggetto specifico della tabella
    @Delete
    suspend fun deleteArticle(article: Article)


}