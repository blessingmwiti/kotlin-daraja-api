package com.blessingmwiti.kotlindarajaapi.domain

import com.blessingmwiti.kotlindarajaapi.util.Resource
import kotlinx.coroutines.flow.Flow

interface MpesaRepository {
    fun getAccessToken(onResult: (Result<String>) -> Unit)
    fun performSTKPush(phoneNumber: String, amount: String, onResult: (Result<STKResponse>) -> Unit)

    fun queryTransaction(checkoutRequestID: String, onResult: (Result<TransactionStatusResponse>) -> Unit)

    fun initializeMpesa(phoneNumber: String, amount: String): Flow<Resource<STKResponse>>
}
