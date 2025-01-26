package com.example.oneminute.ui.fragments

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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.oneminute.databinding.FragmentNearMeFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class NearMe_fragment : Fragment(), LocationListener {

    private var _binding: FragmentNearMeFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationManager: LocationManager

    private var fullAddress: String? = null
    private var city: String? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearMeFragmentBinding.inflate(inflater, container, false)
        locationManager = requireContext().getSystemService(LocationManager::class.java)

        binding.getLocationButton.setOnClickListener {
            checkLocationPermission()
        }

        return binding.root
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
                Log.e("NearMeFragment", "Geocoding error: ${addresses}")

                withContext(Dispatchers.Main) {
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        fullAddress = address.getAddressLine(0) // Indirizzo completo
                        city = address.locality // Nome della città
                        updateLocationUI(latitude, longitude, address)

                        city?.let { onCityReady(it) }
                    } else {
                        updateLocationUI(latitude, longitude, null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.locationTextView.text = "Error retrieving address"
                    Log.e("NearMeFragment", "Geocoding error: ${e.message}")
                }
            }
        }
    }

    private fun updateLocationUI(latitude: Double, longitude: Double, address: Address?) {
        val addressText = address?.getAddressLine(0) ?: "Address not found"
        binding.locationTextView.text = "Lat: $latitude, Lng: $longitude\nAddress: $addressText"
        /***
        Toast.makeText(
            requireContext(),
            "Location: $latitude, $longitude",
            Toast.LENGTH_SHORT
        ).show()
        ***/
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("NearMeFragment", "$provider enabled")
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("NearMeFragment", "$provider disabled")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun doSomethingWithCity() {
        if (city != null) {
            Toast.makeText(requireContext(), "Current city: $city", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "City not available", Toast.LENGTH_SHORT).show()
        }
    }
    private fun onCityReady(city: String) {
        Toast.makeText(requireContext(), "Current city: $city", Toast.LENGTH_SHORT).show()
    }

}