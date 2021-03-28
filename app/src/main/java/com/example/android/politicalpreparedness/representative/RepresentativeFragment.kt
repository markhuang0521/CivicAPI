package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class RepresentativeFragment : BaseFragment() {

    companion object {
        const val Location_PERMISSIONS_REQUEST_CODE = 101
        const val REQUEST_TURN_DEVICE_LOCATION_ON = 202

    }

    override val _viewModel: RepresentativeViewModel by viewModel()
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var currentLocation: Location? = null


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.viewModel = _viewModel

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupLocationClientAndCallback()
        setUpRecyclerView()
        setUpObserver()
        checkDeviceLocationSettings()
        binding.buttonSearch.setOnClickListener {
            if (validateAddress()) {
                _viewModel.address.value?.let {
                    _viewModel.loadRepresentatives(it.toFormattedString())

                }
            }

        }
        binding.buttonLocation.setOnClickListener {
            getLocationToAddress()

        }
    }

    private fun setupLocationClientAndCallback() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    currentLocation = location
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        representativeAdapter = RepresentativeListAdapter()

        binding.recyclerRepresentatives.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = representativeAdapter
        }

    }

    private fun setUpObserver() {
        _viewModel.representatives.observe(viewLifecycleOwner, Observer {
            it?.let {
                representativeAdapter.submitList(it)
            }
        })


    }

    private fun validateAddress(): Boolean {
        if (binding.etAddressLine1.text.isNullOrEmpty()) {
            binding.etAddressLine1.error = "cant be empty"
            return false
        }
        if (binding.etCity.text.isNullOrEmpty()) {
            binding.etCity.error = "cant be empty"
            return false

        }
        if (binding.etZip.text.isNullOrEmpty()) {
            binding.etZip.error = "cant be empty"
            return false

        }
        return true

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Location_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    _viewModel.showToast.postValue("location permission granted")
                } else {
                    snackBarToSetting()

                }
            }
        }

    }

    private fun snackBarToSetting() {
        Snackbar.make(
                requireView(),
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_INDEFINITE
        )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Location_PERMISSIONS_REQUEST_CODE)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        if (PackageManager.PERMISSION_GRANTED ==
                ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                )) {
            return true
        }
        return false
    }

    private fun isLocationServiceOn(): Boolean {

        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    private fun getLocationToAddress() {
        if (checkLocationPermissions() && isLocationServiceOn()) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            _viewModel.address.value = geoCodeLocation(location)


                        } else {
                            if (currentLocation != null) {
                                _viewModel.address.value = geoCodeLocation(currentLocation!!)

                            } else {
                                _viewModel.showErrorMessage.postValue("location not found ")
                            }
                        }

                    }
        } else {
            _viewModel.showErrorMessage.postValue("  please check if GPS is ON! ")


        }
    }


    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermissions() && isLocationServiceOn()) startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper())
    }


    private fun checkDeviceLocationSettings(resolve: Boolean = true) {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
                settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                            requireActivity(),
                            REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.i("Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                        requireView(),
                        R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                startLocationUpdates()
            }
        }
    }


}