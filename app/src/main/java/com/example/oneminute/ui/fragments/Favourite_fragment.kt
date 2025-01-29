package com.example.oneminute.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.oneminute.R
import com.example.oneminute.adapters.NewsAdapter
import com.example.oneminute.databinding.FragmentFavouriteFragmentBinding
import com.example.oneminute.ui.MainActivity_news
import com.example.oneminute.ui.NewsActivity
import com.example.oneminute.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class Favourite_fragment : Fragment(R.layout.fragment_favourite_fragment) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var  binding: FragmentFavouriteFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteFragmentBinding.bind(view)

        newsViewModel = (activity as MainActivity_news).newsViewModel
        setupFavouriteRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply{
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_favourite_fragment_to_articleFragment2,bundle)
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean{
                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view,"Rimosso dai preferiti", Snackbar.LENGTH_LONG).apply {
                    setAction("Annulla"){
                        newsViewModel.addToFavourites(article)
                    }
                    show()
                }
            }

            //in teoria questa funzione non sovrascrive nulla

        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)

        }

        newsViewModel.getFavouritesNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setupFavouriteRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}