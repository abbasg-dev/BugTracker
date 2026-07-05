package com.example.bugtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bugtracker.data.repository.IssueRepository
import com.example.bugtracker.data.local.Issue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IssueViewModel(
    private val repository: IssueRepository
) : ViewModel() {

    private val _issues = MutableStateFlow<List<Issue>>(emptyList())
    val issues: StateFlow<List<Issue>> = _issues.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadIssues()
    }

    fun loadIssues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _issues.value = repository.getIssues()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load issues"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addIssue(issue: Issue) {
        viewModelScope.launch {
            try {
                repository.addIssue(issue)
                loadIssues() // Reload after adding
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add issue"
            }
        }
    }

    // Update function
    fun updateIssue(issue: Issue) {
        viewModelScope.launch {
            try {
                repository.updateIssue(issue)
                loadIssues()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update issue"
            }
        }
    }

    // Delete function
    fun deleteIssue(issue: Issue) {
        viewModelScope.launch {
            try {
                repository.deleteIssue(issue)
                loadIssues()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete issue"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
