package com.example.oneminute.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.oneminute.databinding.FragmentArticleBinding
import com.example.oneminute.models.Article

class ArticleFragment : Fragment() {

    // Usa Safe Args per accedere all'argomento
    private val args: ArticleFragmentArgs by navArgs()
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Gonfia il layout e configura il binding
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        val view = binding.root

        // Ottieni l'articolo dai Safe Args
        val article: Article = args.article
        Log.d("ArticleFragment", "Article: $article")

        // Popola la UI con i dati dell'articolo
        val sourceName = article.source?.name ?: "Fonte sconosciuta"
        binding.articleTitleTextView.text = article.title
        binding.outputTextView.text = """
            Titolo: ${article.title}
            Autore: ${article.author ?: "Autore sconosciuto"}
            Descrizione: ${article.description ?: "Descrizione non disponibile"}
            Contenuto: ${article.content ?: "Contenuto non disponibile"}
        """.trimIndent()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
