package com.example.oneminute.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.oneminute.repository.NewsRepository

class NewsViewModelProviderFactory(
    private val application: Application,
    private val newsRepository: NewsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(application, newsRepository) as T
            }
            modelClass.isAssignableFrom(NewsViewModeNearMe::class.java) -> {
                NewsViewModeNearMe(application, newsRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
