package com.prem.stackframework.network


import com.prem.stackframework.model.StackDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    // Api endpoint
    @GET("")
    suspend fun getStackData(): Response<StackDataResponse>
}