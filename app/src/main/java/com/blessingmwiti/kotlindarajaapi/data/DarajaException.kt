package com.blessingmwiti.kotlindarajaapi.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DarajaException(
    @SerialName("requestId") var requestId: String? = "0",
    @SerialName("errorCode") var errorCode: String? = "0",
    @SerialName("errorMessage") var errorMessage: String? = null
): Exception(errorMessage)