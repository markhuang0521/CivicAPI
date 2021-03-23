package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import java.util.*

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElections(elections: List<Election>)

    @Query("SELECT * FROM election_table where isSaved")
    suspend fun getSavedElections(): List<Election>

    @Query("SELECT * FROM election_table where electionDay>=:today")
    suspend fun getUpcomingElections(today: Date): List<Election>

    @Query("SELECT * FROM election_table where id=:id limit 1")
    suspend fun getElectionById(id: String): Election

    @Delete
    suspend fun deleteElection(election: Election)

    @Query("DELETE FROM election_table WHERE electionDay < :today AND NOT isSaved")
    suspend fun deletePastUnsavedElections(today: Date)


}