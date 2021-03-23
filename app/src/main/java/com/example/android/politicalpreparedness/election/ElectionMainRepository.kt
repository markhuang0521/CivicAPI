package com.example.android.politicalpreparedness.database

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.getToday
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionMainRepository(private val electionDao: ElectionDao,
                             private val civicsApi: CivicsApiService,
                             private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ElectionDataSource {

    override suspend fun getUpcomingElection(): List<Election> = withContext(ioDispatcher) {
        return@withContext (electionDao.getUpcomingElections(getToday()))
    }

    override suspend fun getSaveElection(): List<Election> = withContext(ioDispatcher) {

        return@withContext (electionDao.getUpcomingElections(getToday()))

    }

    override suspend fun refreshElection() = withContext(ioDispatcher) {
        deleteNotSaveElection()

        val electionResponse: ElectionResponse = civicsApi.getElections()
        val elections = electionResponse.elections
        electionDao.insertElections(elections)
    

    }


    override suspend fun getVoterInfo(address: String, electionId: Int): VoterInfoResponse = withContext(ioDispatcher) {
        return@withContext civicsApi.getVoterInfo(address, electionId)
    }


    override suspend fun getRepresentative(address: String): List<Representative> = withContext(ioDispatcher) {


        val response = civicsApi.getRepresentativesByAddress(address)
        val offices = response.offices
        val officials = response.officials
        val representatives = offices.flatMap { office -> office.getRepresentatives(officials) }

        return@withContext representatives


    }

    override suspend fun saveElection(election: Election) = withContext(ioDispatcher)
    {
        electionDao.saveElection(election)
    }

    override suspend fun deleteNotSaveElection() = withContext(ioDispatcher) {
        electionDao.deletePastUnsavedElections(getToday())
    }
}