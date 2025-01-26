package com.example.oneminute.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.oneminute.BuildConfig
import com.example.oneminute.databinding.FragmentArticleBinding
import com.example.oneminute.models.Article
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.*

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleFragmentArgs by navArgs()

    private val initialPrompt = """
        Scrivi un riassunto chiaro e conciso di una notizia basata su queste informazioni: il titolo fornisce il tema principale, l'autore e la fonte indicano la credibilità della notizia, la descrizione fornisce un contesto introduttivo e il contenuto, anche se parziale, offre dettagli aggiuntivi. Il riassunto deve essere lungo circa 120-150 parole, sufficienti per una lettura di un minuto, e includere i punti salienti della notizia come chi, cosa, dove, quando, perché e come. Evita ripetizioni, usa un linguaggio semplice e diretto, e sintetizza le informazioni principali per creare un testo informativo e leggibile. 
    """.trimIndent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val view = binding.root

        // Ottieni l'articolo passato tramite Safe Args
        val article: Article = args.article

        // Costruisci il prompt iniziale con le informazioni dell'articolo
        val articleInfo = """
            Informazioni sull'articolo:
            - Titolo: ${article.title}
            - Autore: ${article.author ?: "Autore sconosciuto"}
            - Fonte: ${article.source?.name ?: "Fonte sconosciuta"}
            - Descrizione: ${article.description ?: "Descrizione non disponibile"}
            - Contenuto: ${article.content ?: "Contenuto non disponibile"}
        """.trimIndent()

        // Combina il prompt iniziale con i dettagli dell'articolo
        val fullPrompt = "$initialPrompt\n\n$articleInfo\n\nRisposta:"
        binding.articleTitleTextView.text = "${article.title}"
        // Genera automaticamente la risposta
        generateResponse(fullPrompt)

        return view
    }

    private fun generateResponse(prompt: String) {
        // Mostra il caricamento
        binding.loadingProgressBar.visibility = View.VISIBLE
        binding.outputTextView.text = "Generazione in corso, attendere..."

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.API_KEY
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(prompt)
                }
                // Mostra la risposta generata
                binding.outputTextView.text = response.text
            } catch (e: Exception) {
                Toast.makeText(context, "Errore nella generazione della risposta", Toast.LENGTH_SHORT).show()
            } finally {
                // Nascondi il caricamento
                binding.loadingProgressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
