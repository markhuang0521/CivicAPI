package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.base.SingleLiveEvent
import com.example.android.politicalpreparedness.database.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch


//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(app: Application, private val repository: ElectionDataSource) : BaseViewModel(app) {

    val upcomingElections = MutableLiveData<List<Election>>()
    val saveElections = MutableLiveData<List<Election>>()
    val nagivateToVoterInfo = SingleLiveEvent<Election>()


    init {

        viewModelScope.launch {
            repository.refreshElection()

        }


    }


    fun loadUpcomingElections() {
        showLoading.value = true
        viewModelScope.launch {
            upcomingElections.value = repository.getUpcomingElection()
            showLoading.postValue(false)

            invalidateShowNoData()


        }
    }

    fun loadSaveElections() {
        showLoading.value = true

        viewModelScope.launch {
            saveElections.value = repository.getSaveElection()
            showLoading.postValue(false)
            invalidateShowNoData()

        }
    }

    fun onElectionSelect(election: Election) {
        nagivateToVoterInfo.value = election
    }


    private fun invalidateShowNoData() {
        showNoData.value = saveElections.value == null || saveElections.value!!.isEmpty()
    }

    //TODO: Create live data val for upcoming elections

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    //TODO: Create functions to navigate to saved or upcoming election voter info

}