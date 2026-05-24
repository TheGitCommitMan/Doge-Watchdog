package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE vote = 'PENDING' ORDER BY isCurated DESC, timestamp DESC")
    fun getPendingTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE vote != 'PENDING' ORDER BY timestamp DESC")
    fun getVotedTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("UPDATE transactions SET vote = :vote WHERE transactionId = :id")
    suspend fun updateVote(id: String, vote: String)

    @Query("UPDATE transactions SET vote = 'PENDING'")
    suspend fun resetAllVotes()

    @Query("DELETE FROM transactions WHERE isCurated = 0")
    suspend fun deleteLiveTransactions()
}
