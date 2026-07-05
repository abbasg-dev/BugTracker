package com.example.bugtracker.data.local

import androidx.room.*

@Dao
interface IssueDao {

    @Query("SELECT * FROM issues ORDER BY createdAt DESC")
    suspend fun getAllIssues(): List<Issue>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssue(issue: Issue)

    @Update
    suspend fun updateIssue(issue: Issue)

    @Delete
    suspend fun deleteIssue(issue: Issue)

    @Query("SELECT * FROM issues WHERE isSynced = 0")
    suspend fun getUnsyncedIssues(): List<Issue>
}
