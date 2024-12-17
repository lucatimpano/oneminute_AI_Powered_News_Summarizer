package com.example.oneminute.util

// Classe sigillata (sealed class) che rappresenta lo stato di una risorsa generica T
sealed class Resource<T>(val data: T? = null, val message: String? = null) {

    // Sottoclasse per rappresentare il successo: contiene il messaggio e i dati caricati
    class Succes<T>(message: String, data: T? = null): Resource<T>(data, message)

    // Sottoclasse per rappresentare un errore: include il messaggio e, opzionalmente, i dati
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)

    // Sottoclasse per rappresentare uno stato di caricamento (loading)
    class Loading<T>: Resource<T>()
}
