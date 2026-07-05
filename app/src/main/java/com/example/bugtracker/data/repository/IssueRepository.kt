package com.example.bugtracker.data.repository

import com.example.bugtracker.data.local.IssueDao
import com.example.bugtracker.data.local.Issue
import com.example.bugtracker.data.remote.RetrofitClient
import com.example.bugtracker.data.remote.IssueDto
import kotlinx.coroutines.delay

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

    suspend fun syncPendingIssues(retry: Int = 3) {
        val unsynced = dao.getUnsyncedIssues()

        unsynced.forEach { issue ->
            try {
                // IssueDto expects id: Int?, not issue.id directly
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
                    delay(2000)
                    syncPendingIssues(retry - 1)
                }
                // Optional: Log the error
                // Log.e("IssueRepository", "Sync failed for issue ${issue.id}", e)
            }
        }
    }
}
