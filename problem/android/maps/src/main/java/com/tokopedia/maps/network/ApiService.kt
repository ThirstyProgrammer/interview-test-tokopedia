package com.tokopedia.maps.network

import com.tokopedia.maps.data.Country
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {

    companion object {
        const val RAPID_API_KEY = "X-RapidAPI-Key"
        const val RAPID_API_KEY_VALUE = "44bf87fddemsh2092831e863d5b9p13b451jsn4c4715c66b26"
        const val RAPID_API_HOST = "X-RapidAPI-Host"
        const val RAPID_API_HOST_VALUE = "restcountries-v1.p.rapidapi.com"
    }

    @GET("name/{country}")
    fun getCountryDetail(
        @Header(RAPID_API_KEY) key: String,
        @Header(RAPID_API_HOST) host: String,
        @Path("country") country: String
    ): Call<List<Country>>
}

