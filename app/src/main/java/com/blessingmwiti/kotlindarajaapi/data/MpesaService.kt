package com.blessingmwiti.kotlindarajaapi.data

import com.blessingmwiti.kotlindarajaapi.domain.AccessToken
import com.blessingmwiti.kotlindarajaapi.domain.STKPush
import com.blessingmwiti.kotlindarajaapi.domain.STKResponse
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusQuery
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaService {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    fun getAccessToken(
        @Header("Authorization") authHeader: String
    ): Call<AccessToken>

    @POST("mpesa/stkpush/v1/processrequest")
    fun performSTKPush(
        @Header("Authorization") authHeader: String,
        @Body stkPush: STKPush
    ): Call<STKResponse>

    @POST("mpesa/transactionstatus/v1/query")
    fun queryTransaction(
        @Header("Authorization") authHeader: String,
        @Body query: TransactionStatusQuery
    ): Call<TransactionStatusResponse>
}
