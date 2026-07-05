package com.example.bugtracker.data.repository

import com.example.bugtracker.data.local.IssueDao
import com.example.bugtracker.data.local.Issue
import com.example.bugtracker.data.remote.RetrofitClient
import com.example.bugtracker.data.remote.IssueDto
import kotlinx.coroutines.delay
import kotlin.math.pow

class IssueRepository(
    private val dao: IssueDao
) {

    suspend fun getIssues(): List<Issue> {
        return dao.getAllIssues()
    }

    suspend fun addIssue(issue: Issue) {
        dao.insertIssue(issue)
        syncPendingIssues()
    }

    suspend fun updateIssue(issue: Issue) {
        dao.updateIssue(issue)
        syncPendingIssues()
    }

    suspend fun deleteIssue(issue: Issue) {
        dao.deleteIssue(issue)
        syncPendingIssues()
    }

    // Q3: Hotfix - Improved retry with exponential backoff
    suspend fun syncPendingIssues(retry: Int = 3) {
        val unsynced = dao.getUnsyncedIssues()

        unsynced.forEach { issue ->
            try {
                RetrofitClient.api.createIssue(
                    IssueDto(
                        id = if (issue.id != 0) issue.id else null,
                        title = issue.title,
                        description = issue.description,
                        priority = issue.priority,
                        status = issue.status,
                        createdAt = issue.createdAt
                    )
                )
                // Mark as synced after successful API call
                dao.updateIssue(issue.copy(isSynced = true))
            } catch (e: Exception) {
                if (retry > 0) {
                    // Exponential backoff: 2^retry * 1000ms
                    val delayTime = (2.0.pow(3 - retry) * 1000).toLong()
                    delay(delayTime)
                    syncPendingIssues(retry - 1)
                }
                // Log error if needed
            }
        }
    }
}