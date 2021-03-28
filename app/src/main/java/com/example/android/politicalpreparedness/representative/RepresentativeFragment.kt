package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class RepresentativeFragment : BaseFragment() {

    companion object {
        const val Location_PERMISSIONS_REQUEST_CODE = 101
    }

    override val _viewModel: RepresentativeViewModel by viewModel()
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var representativeAdapter: RepresentativeListAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.viewModel = _viewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setUpRecyclerView()
        setUpObserver()
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
        _viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let {

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

    @SuppressLint("MissingPermission")
    private fun getLocationToAddress() {
        if (checkLocationPermissions()) {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            _viewModel.address.value = geoCodeLocation(location)


                        } else {
                            _viewModel.showErrorMessage.postValue("location not found")
                        }

                    }
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

}