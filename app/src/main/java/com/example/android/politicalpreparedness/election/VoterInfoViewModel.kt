package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.base.BaseViewModel
import com.example.android.politicalpreparedness.database.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(app: Application, private val repository: ElectionDataSource) : BaseViewModel(app) {

    val voterInfo = MutableLiveData<VoterInfoResponse>()
    val selectedElection = MutableLiveData<Election>()


    fun getVoterInfo(election: Election) {
        val address = "23131"
        val id = election.id
        viewModelScope.launch {
            try {
                val voterInfoResponse = repository.getVoterInfo(address, id)
                voterInfo.value = voterInfoResponse
            } catch (e: Exception) {
                Timber.i(e.localizedMessage)
            }
        }
    }

    fun saveElection(election: Election) {
        election.isSaved = !election.isSaved

        viewModelScope.launch {
            repository.saveElection(election)
        }

    }

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}