package com.vishnu.privateexpensetracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchant: String,
    val amount: Double,
    val currency: String = "INR",
    val timestamp: Long,
    val category: String = "Uncategorized",
    val source: String = "Manual",
    val reference: String = "",
    val confidence: Double = 0.0
)
