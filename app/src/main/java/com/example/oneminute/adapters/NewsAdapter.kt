package com.example.oneminute.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oneminute.R
import com.example.oneminute.models.Article

// Adapter per la RecyclerView che mostra un elenco di articoli
class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    // ViewHolder: classe interna per gestire la singola view di un articolo
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Variabili lateinit per le viste che compongono un articolo
    lateinit var  articleImage: ImageView // Immagine dell'articolo
    lateinit var articleSource: TextView // Fonte dell'articolo
    lateinit var articleTitle: TextView // Titolo dell'articolo
    lateinit var articleDescription: TextView // Descrizione dell'articolo
    lateinit var articleDateTime: TextView // Data e ora dell'articolo

    // DifferCallback: confronto ottimizzato tra vecchi e nuovi dati per aggiornare la RecyclerView
    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean{
            // Controlla se due articoli hanno lo stesso URL, identificandoli come "uguali"
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            // Controlla se il contenuto di due articoli è esattamente lo stesso
            return oldItem == newItem
        }
    }

    // AsyncListDiffer: gestisce l'aggiornamento della lista in background in modo efficiente
    val differ = AsyncListDiffer(this, differCallback)

    // Creazione del ViewHolder: infla il layout della singola view (item_news.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_news , parent, false)
        )
    }

    // Restituisce il numero di articoli presenti nella lista
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Listener per la gestione del click su un articolo
    private var onItemClickListener: ((Article) -> Unit)? = null

    // Binding del ViewHolder: assegna i dati alle view corrispondenti
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        // Collega le view con i rispettivi ID
        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleSource = holder.itemView.findViewById(R.id.articleSource)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)

        // Applica i dati dell'articolo alla UI utilizzando Glide per le immagini
        holder.itemView.apply{
            Glide.with(this).load(article.urlToImage).into(articleImage) // Carica l'immagine
            articleSource.text = article.source?.name // Mostra la fonte
            articleTitle.text = article.title // Mostra il titolo
            articleDescription.text = article.description // Mostra la descrizione
            articleDateTime.text = article.publishedAt // Mostra la data e ora

            // Imposta un listener per il click sull'intero elemento
            setOnClickListener{
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
    }

    // Funzione per impostare un listener per il click sugli articoli
    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }
}
