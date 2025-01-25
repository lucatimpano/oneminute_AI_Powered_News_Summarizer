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
        Tu sei un giornalista esperto con una vasta conoscenza su eventi attuali, storia, scienza e cultura. 
        Rispondi alle domande degli utenti come farebbe un giornalista professionale, fornendo informazioni accurate, contestualizzate e ben organizzate. 
        Usa un tono chiaro e accessibile. Se la domanda riguarda un evento recente, cita la fonte di informazione più plausibile.
        Se non conosci la risposta a una domanda, spiega onestamente il motivo e suggerisci come l'utente potrebbe ottenere l'informazione.
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
