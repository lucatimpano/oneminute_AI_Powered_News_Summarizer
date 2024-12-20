package com.example.oneminute.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.oneminute.models.Article
import com.example.oneminute.models.NewsResponse
import com.example.oneminute.repository.NewsRepository
import com.example.oneminute.util.Resource
import kotlinx.coroutines.launch
import okhttp3.Response
import okio.IOException
import java.util.Locale.IsoCountryCode


//serve per gestire i dati relativi alle notizie

class NewsViewModel(application: Application, val newsRepository: NewsRepository):AndroidViewModel(application) {

//la News View Model interagisce con il Repository (NewsRepository) per recuperare i dati e poi li espone alla UI
// tramite oggetti di tipo MutableLiveData<Resource<NewsResponse>>, che rappresentano lo stato corrente
//(caricamento, successo o errore) delle notizie. Ogni volta che i dati cambiano, la UI sarà
//automaticamente aggiornata grazie all'osservazione di LiveData.

    val headlines:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinesPage = 1 //indica il n° della pagina corrente per le notizie principali
    var headlinesResponse: NewsResponse? = null //memorizza le risposte

    val searchNews:MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1 //indica il n° della pagina corrente per le notizie ricercate
    var searchNewsResponse: NewsResponse? = null
    var newSearchQuery:String? =null //query di ricerca corrente
    var oldSearchQuery:String?= null //query di ricerca precedente


    init{
        getHeadlines("us")
    }

    fun getHeadlines(coutryCode:String)=viewModelScope.launch {
        headlinesInternet(coutryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        headlinesInternet(searchQuery)
    }


//se registriamo un successo, incrementiamo la pagina corrente, se headlinesresponse che è la prima
//richiesta è null viene aggiornata, se ci sono già notizie memorizzate, i nuovi articoli vengono
//aggiunti alla lista

//Alla fine, restituisce un oggetto Resource.Success con i dati delle notizie, altrimenti, se c'è stato
// un errore, restituisce un Resource.Error con il messaggio di errore.
    private fun handleHeadLinesResponse(response: retrofit2.Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{resultResponse ->
                headlinesPage++
                if(headlinesResponse == null){
                    headlinesResponse=resultResponse
                }else{
                    val oldArticles=headlinesResponse?.articles
                    val newArticles= resultResponse.articles
                    oldArticles?.addAll(newArticles)

                }
                return Resource.Success(headlinesResponse ?:resultResponse )
            }
        }
        return Resource.Error(response.message())
    }


    private fun handleSearchNewsResponse(response: retrofit2.Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let{resultResponse ->
                headlinesPage++
                if(searchNewsResponse == null || newSearchQuery !=oldSearchQuery){
                    searchNewsPage=1
                    oldSearchQuery= newSearchQuery
                    searchNewsResponse=resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse ?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)

                }
                return Resource.Success(searchNewsResponse ?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun addToFavourites(article: Article)= viewModelScope.launch {
        newsRepository.upsert(article)

    }

    fun getFavouritesNews()=newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article)=viewModelScope.launch {
        newsRepository.deleteArticle(article)

    }

    //verifica la connessione su dispositivi android
    fun internetConnection(context: Context):Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager).apply{
            return getNetworkCapabilities(activeNetwork)?.run{

                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
                    else -> false

                }

            }?:false
        }
    }

    private suspend fun headlinesInternet(countryCode: String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response=newsRepository.getHeadlines(countryCode,headlinesPage)
                headlines.postValue(handleHeadLinesResponse(response))
            }else{
                headlines.postValue(Resource.Error("No internet connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("Unable to connect"))
                else -> headlines.postValue((Resource.Error("No signal")))
            }
        }
    }

    private suspend fun searchNewsInternet(searchQuery:String){
        newSearchQuery=searchQuery
        searchNews.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response=newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))

            }else{
                searchNews.postValue(Resource.Error("No internet connection"))
            }
        }catch (t:Throwable){
            when(t){
                is IOException ->searchNews.postValue(Resource.Error("Unable to connect"))
                else -> searchNews.postValue((Resource.Error("No signal")))
            }
        }
    }

}