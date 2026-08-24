package com.vishnu.privateexpensetracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    suspend fun all(): List<Expense>

    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE timestamp >= :from AND timestamp < :to")
    suspend fun total(from: Long, to: Long): Double
}
