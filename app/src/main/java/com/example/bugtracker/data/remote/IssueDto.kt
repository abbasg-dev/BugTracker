package com.example.bugtracker.data.remote

data class IssueDto(
    val id: Int?,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val createdAt: Long
)
