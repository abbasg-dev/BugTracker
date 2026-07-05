package com.example.bugtracker.data.remote

import retrofit2.http.*

interface IssueApi {

    @GET("issues")
    suspend fun getIssues(): List<IssueDto>

    @POST("issues")
    suspend fun createIssue(@Body issue: IssueDto)

    @PUT("issues/{id}")
    suspend fun updateIssue(@Path("id") id: Int, @Body issue: IssueDto)

    @DELETE("issues/{id}")
    suspend fun deleteIssue(@Path("id") id: Int)
}
