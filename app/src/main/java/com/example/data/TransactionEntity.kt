package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val transactionId: String,
    val awardId: String,
    val title: String,
    val description: String,
    val amount: Double,
    val agencyName: String,
    val subAgencyName: String,
    val recipientName: String,
    val startDate: String,
    val endDate: String,
    val category: String, // e.g. "Defense", "Health", "Space", "Science", "Education", "Other"
    val absurdityReason: String, // Funny explanation of why this represents waste or is funny
    val vote: String = "PENDING", // "PENDING", "WASTE", "VALID"
    val isCurated: Boolean = false, // Curated highlights
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
