package id.ac.unri.storyapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unri.storyapp.R
import id.ac.unri.storyapp.databinding.ActivityMapsBinding
import id.ac.unri.storyapp.ui.MainActivity.Companion.EXTRA_TOKEN
import id.ac.unri.storyapp.utils.Message
import id.ac.unri.storyapp.viewmodel.MapsViewModel
import kotlinx.coroutines.launch

@ExperimentalPagingApi
@Suppress("DEPRECATION")
@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var binding: ActivityMapsBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mapsViewModel: MapsViewModel by viewModels()

    private var token: String = ""

    companion object{
        const val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.map)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        styleMap()

        lifecycleScope.launch {
            launch {
                mapsViewModel.getAuthToken().collect { itToken ->
                    if(!itToken.isNullOrEmpty()) token = itToken
                    getLocation()
                    markLocation()
                }
            }
        }

    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
            this@MapsActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if(location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                } else {
                    Message.setMessage(this@MapsActivity , resources.getString(R.string.warning_active_location))
                }
            }
        }else{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                getLocation()
            }
        }

    private fun markLocation() {
        lifecycleScope.launchWhenResumed {
            launch {
                mapsViewModel.getStoriesLocation(token).collect { result ->
                    result.onSuccess { response ->
                        response.listStory?.forEach { story ->
                            if(story?.lat != null && story.lon != null) {
                                val latLng = LatLng(story.lat, story.lon)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(story.name)
                                        .snippet("Lat : ${story.lat}, Lon : ${story.lon}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun styleMap() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, resources.getString(R.string.warning_active_location))
            }
        }catch (exception: Resources.NotFoundException) {
            Log.e(TAG, resources.getString(R.string.error_map_style))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}