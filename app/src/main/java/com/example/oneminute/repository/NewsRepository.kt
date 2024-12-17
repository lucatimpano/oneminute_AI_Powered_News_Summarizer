package com.example.oneminute.repository

import com.example.oneminute.API.RetrofitInstance
import com.example.oneminute.db.ArticleDatabase
import com.example.oneminute.models.Article

// La classe NewsRepository funge da strato intermedio tra i dati e la ViewModel
// Gestisce l'accesso ai dati provenienti da API remote o dal database locale
class NewsRepository(val db: ArticleDatabase) {

    // Funzione sospesa per ottenere i titoli delle notizie dall'API
    // Parametri:
    // - countryCode: codice del paese (es. "us" per Stati Uniti)
    // - pageNumber: numero della pagina per la paginazione dei risultati
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)

    // Funzione sospesa per cercare notizie nell'API in base a una query di ricerca
    // Parametri:
    // - searchQuery: testo della query da cercare
    // - pageNumber: numero della pagina per la paginazione dei risultati
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    // Funzione per inserire o aggiornare un articolo nel database locale
    // Parametro:
    // - article: oggetto Article da inserire o aggiornare
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    // Funzione per recuperare tutti gli articoli preferiti salvati nel database locale
    // Restituisce una lista osservabile (LiveData o Flow) di articoli
    fun getFavouriteNews() = db.getArticleDao().getAlleArticles()

    // Funzione sospesa per eliminare un articolo dal database locale
    // Parametro:
    // - article: oggetto Article da eliminare
    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}
