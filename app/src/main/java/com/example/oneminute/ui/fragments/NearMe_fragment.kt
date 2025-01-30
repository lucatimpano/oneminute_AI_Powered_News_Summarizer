package com.example.oneminute.ui.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oneminute.R
import com.example.oneminute.adapters.NewsAdapter
import com.example.oneminute.databinding.FragmentNearMeFragmentBinding
import com.example.oneminute.databinding.FragmentSearchFragmentBinding
import com.example.oneminute.ui.MainActivity_news
import com.example.oneminute.ui.NewsViewModel
import com.example.oneminute.util.Constants
import com.example.oneminute.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.oneminute.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class NearMe_fragment : Fragment(R.layout.fragment_near_me_fragment), LocationListener {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: ImageButton
    lateinit var errorText: TextView
    lateinit var itemSearchError: CardView

    private lateinit var binding: FragmentNearMeFragmentBinding
    private lateinit var locationManager: LocationManager

    private var fullAddress: String? = null
    private var city: String? = null
    private var isError = false
    private var isLoading = false
    private var isLastPage = false
    private var isScrolling = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Binding della vista principale
        binding = FragmentNearMeFragmentBinding.bind(view)
        locationManager = requireContext().getSystemService(LocationManager::class.java)
        binding.searchEdit.visibility = View.GONE
        checkLocationPermission()
        // Configura il bottone per la localizzazione
        /***
        binding.getLocationButton.setOnClickListener {

        }
        ***/

        // Inflate e setup di elementi di errore da un layout separato
        val inflatedView: View = layoutInflater.inflate(R.layout.no_internet_error, null)
        itemSearchError = view.findViewById(R.id.itemSearchError)
        retryButton = inflatedView.findViewById<ImageButton>(R.id.retryButton)
        errorText = inflatedView.findViewById(R.id.errorText)

        // Configura il ViewModel
        newsViewModel = (activity as MainActivity_news).newsViewModel
        setupNearMeRecycler()

        if (binding.searchEdit.text.toString().isNotEmpty()) {
            binding.searchEdit.text?.clear() // Pulisce il campo di ricerca
            newsAdapter.differ.submitList(emptyList()) // Svuota la lista
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_nearMe_fragment_to_articleFragment, bundle)
        }

        // Gestione della barra di ricerca
        var job: Job? = null
        binding.searchEdit.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        // Osserva i cambiamenti nei dati di ricerca
        newsViewModel.searchNews.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPages
                        if (isLastPage) {
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "Error: $message", Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        // Gestione del pulsante Retry
        retryButton.setOnClickListener {
            if (binding.searchEdit.text.toString().isNotEmpty()) {
                newsViewModel.searchNews(binding.searchEdit.text.toString())
            } else {
                hideErrorMessage()
            }
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.loadingSpinner.visibility = View.VISIBLE
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    5f,
                    this
                )
            }
        } catch (e: SecurityException) {
            Log.e("NearMeFragment", "Location permission error: ${e.message}")
        }
    }

    override fun onLocationChanged(location: Location) {
        locationManager.removeUpdates(this)

        val latitude = location.latitude
        val longitude = location.longitude

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                withContext(Dispatchers.Main) {
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        fullAddress = address.getAddressLine(0)
                        city = address.locality
                        updateLocationUI(latitude, longitude, address)
                        city?.let { onCityReady(it) }
                    } else {
                        updateLocationUI(latitude, longitude, null)
                    }
                    binding.loadingSpinner.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("NearMeFragment", "Geocoding error: ${e.message}")
                    binding.loadingSpinner.visibility = View.GONE
                }
            }
        }
    }

    private fun updateLocationUI(latitude: Double, longitude: Double, address: Address?) {
        val addressText = address?.getAddressLine(0) ?: "Address not found"
        // Puoi aggiornare qui la UI
    }

    private fun onCityReady(city: String) {
        binding.searchEdit.setText(city)
        newsViewModel.searchNews(city)
        Toast.makeText(requireContext(), "Searching news for city: $city", Toast.LENGTH_SHORT).show()
    }

    private fun setupNearMeRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@NearMe_fragment.scrollListener)
        }
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("NearMeFragment", "$provider enabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("NearMeFragment", "$provider disabled")
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoError = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoError && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}
