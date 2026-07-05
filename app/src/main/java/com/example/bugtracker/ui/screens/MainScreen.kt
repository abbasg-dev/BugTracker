package com.example.bugtracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bugtracker.data.local.Issue
import com.example.bugtracker.ui.viewmodel.IssueViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: IssueViewModel = viewModel()
) {
    // Observe the StateFlow from ViewModel
    val issues by viewModel.issues.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var selectedStatus by remember { mutableStateOf("Open") }
    var expandedPriority by remember { mutableStateOf(false) }

    val priorities = listOf("High", "Medium", "Low")
    val statuses = listOf("Open", "In Progress", "Closed")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bug Tracker") },
                actions = {
                    IconButton(onClick = { viewModel.loadIssues() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Input Section
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Issue Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Priority Dropdown - Fixed
            Column {
                Text(
                    text = "Priority: $selectedPriority",
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = { expandedPriority = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Priority")
                }
                DropdownMenu(
                    expanded = expandedPriority,
                    onDismissRequest = { expandedPriority = false }
                ) {
                    priorities.forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(priority) },
                            onClick = {
                                selectedPriority = priority
                                expandedPriority = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val issue = Issue(
                            title = title,
                            description = description.ifBlank { "No description" },
                            priority = selectedPriority,
                            status = selectedStatus,
                            createdAt = System.currentTimeMillis()
                        )
                        viewModel.addIssue(issue)
                        title = ""
                        description = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Issue")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading State
            if (isLoading) {
                CircularProgressIndicator()
            }

            // Error Message
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Issues List
            if (issues.isEmpty() && !isLoading) {
                Text(
                    text = "No issues found. Add one above!",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn {
                    items(issues) { issue ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (issue.priority) {
                                    "High" -> MaterialTheme.colorScheme.errorContainer
                                    "Medium" -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.primaryContainer
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = issue.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Priority: ${issue.priority} | Status: ${issue.status}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = issue.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Button(
                                    onClick = { viewModel.deleteIssue(issue) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
