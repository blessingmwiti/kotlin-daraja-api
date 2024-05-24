package com.blessingmwiti.kotlindarajaapi.presentation.payment

import com.blessingmwiti.kotlindarajaapi.domain.STKResponse
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusResponse

data class PaymentUiState(
    val loading: Boolean = false,
    val stkPushResponse: STKResponse? = null,
    val transactionStatusResponse: TransactionStatusResponse? = null,
    val error: String = "",
    val phoneNumber: String = "",
    val amount: String = ""
)
