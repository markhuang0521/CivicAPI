package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.database.ElectionDataSource
import com.example.android.politicalpreparedness.database.Result
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch


class RepresentativeViewModel(app: Application, private val repository: ElectionDataSource) : BaseViewModel(app) {

    val representatives = MutableLiveData<List<Representative>>()
    val address = MutableLiveData<Address>()

    fun loadRepresentatives(address: String) {
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.getRepresentative(address)
            showLoading.postValue(false)
            when (result) {
                is Result.Success<List<Representative>> -> {
                    representatives.value = result.data
                }
                is Result.Error -> {
                    representatives.value = emptyList()

                    showSnackBar.value = result.message

                }
            }

            invalidateShowNoData()
        }

    }


    private fun invalidateShowNoData() {
        showNoData.value = representatives.value == null || representatives.value!!.isEmpty()
    }


    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address


    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
