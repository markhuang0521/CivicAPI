package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative

interface ElectionDataSource {

    suspend fun getUpcomingElection(): List<Election>
    suspend fun getSaveElection(): List<Election>
    suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse
    suspend fun getRepresentative(address: String): Result<List<Representative>>
    suspend fun saveElection(election: Election)
    suspend fun deleteNotSaveElection()
    suspend fun refreshElection()
}