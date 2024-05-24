package com.blessingmwiti.kotlindarajaapi.domain

import androidx.annotation.Keep


@Keep
data class AccessToken(
    val access_token: String,
    val expires_in: String
)

@Keep
data class STKPush(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val TransactionType: String,
    val Amount: String,
    val PartyA: String,
    val PartyB: String,
    val PhoneNumber: String,
    val CallBackURL: String,
    val AccountReference: String,
    val TransactionDesc: String
)

@Keep
data class STKResponse(
    val MerchantRequestID: String,
    val CheckoutRequestID: String,
    val ResponseCode: String,
    val ResponseDescription: String,
    val CustomerMessage: String
)

@Keep
data class TransactionStatusQuery(
    val BusinessShortCode: String,
    val Password: String,
    val Timestamp: String,
    val CheckoutRequestID: String,
)
@Keep
data class TransactionStatusResponse(
    val ResponseCode: String,
    val ResponseDescription: String,
    val MerchantRequestID: String,
    val CheckoutRequestID: String
)
