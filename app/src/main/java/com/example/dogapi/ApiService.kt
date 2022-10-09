package com.example.dogapi

import com.example.dogapi.model.Dog
import com.example.dogapi.model.Raza
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getRandomImg(@Url url: String): Response<Dog>

    @GET
    suspend fun getListImg(@Url url: String): Response<Raza>
}