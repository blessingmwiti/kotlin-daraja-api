package com.blessingmwiti.kotlindarajaapi.data

import android.util.Base64
import com.blessingmwiti.kotlindarajaapi.domain.AccessToken
import com.blessingmwiti.kotlindarajaapi.BuildConfig
import com.blessingmwiti.kotlindarajaapi.domain.STKPush
import com.blessingmwiti.kotlindarajaapi.domain.STKResponse
import com.blessingmwiti.kotlindarajaapi.domain.MpesaRepository
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusQuery
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusResponse
import com.blessingmwiti.kotlindarajaapi.util.Resource
import com.blessingmwiti.kotlindarajaapi.util.getDarajaPhoneNumber
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MpesaRepositoryImpl(private val mpesaService: MpesaService) : MpesaRepository {

    override fun getAccessToken(onResult: (Result<String>) -> Unit) {
        val appKey = BuildConfig.MPESA_APP_KEY
        val appSecret = BuildConfig.MPESA_APP_SECRET
        val authString = "$appKey:$appSecret"
        val authHeader = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)

        val call = mpesaService.getAccessToken(authHeader)
        call.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    val accessToken = response.body()?.access_token ?: ""
                    onResult(Result.success(accessToken))
                } else {
                    onResult(Result.failure(Exception("Failed to get access token")))
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                onResult(Result.failure(t))
            }
        })
    }

    override fun performSTKPush(
        phoneNumber: String,
        amount: String,
        onResult: (Result<STKResponse>) -> Unit
    ) {
        getAccessToken { result ->
            result.onSuccess { accessToken ->
                val timestamp =
                    SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = Base64.encodeToString(
                    (BuildConfig.MPESA_SHORTCODE + BuildConfig.MPESA_PASSKEY + timestamp).toByteArray(),
                    Base64.NO_WRAP
                )

                val stkPush = STKPush(
                    BusinessShortCode = BuildConfig.MPESA_SHORTCODE,
                    Password = password,
                    Timestamp = timestamp,
                    TransactionType = "CustomerPayBillOnline",
                    Amount = amount,
                    PartyA = phoneNumber.getDarajaPhoneNumber(),
                    PartyB = BuildConfig.MPESA_SHORTCODE,
                    PhoneNumber = phoneNumber.getDarajaPhoneNumber(),
                    CallBackURL = BuildConfig.MPESA_CALLBACK_URL,
                    AccountReference = BuildConfig.MPESA_ACCOUNT_REFERENCE,
                    TransactionDesc = "Test Payment"
                )

                val authHeader = "Bearer $accessToken"
                val call = mpesaService.performSTKPush(authHeader, stkPush)
                call.enqueue(object : Callback<STKResponse> {
                    override fun onResponse(
                        call: Call<STKResponse>,
                        response: Response<STKResponse>
                    ) {
                        if (response.isSuccessful) {
                            val stkResponse = response.body()
                            onResult(Result.success(stkResponse!!))
                        } else {
                            onResult(Result.failure(Exception("STK Push Failed")))
                        }
                    }

                    override fun onFailure(call: Call<STKResponse>, t: Throwable) {
                        onResult(Result.failure(t))
                    }
                })
            }.onFailure {
                onResult(Result.failure(it))
            }
        }
    }

    override fun queryTransaction(checkoutRequestID: String, onResult: (Result<TransactionStatusResponse>) -> Unit) {
        getAccessToken { result ->
            result.onSuccess { accessToken ->
                val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                val password = Base64.encodeToString(
                    (BuildConfig.MPESA_SHORTCODE + BuildConfig.MPESA_PASSKEY + timestamp).toByteArray(),
                    Base64.NO_WRAP
                )

                val query = TransactionStatusQuery(
                    BusinessShortCode = BuildConfig.MPESA_SHORTCODE,
                    Password = password,
                    Timestamp = timestamp,
                    CheckoutRequestID = checkoutRequestID,
                )

                val authHeader = "Bearer $accessToken"
                val call = mpesaService.queryTransaction(authHeader, query)
                call.enqueue(object : Callback<TransactionStatusResponse> {
                    override fun onResponse(call: Call<TransactionStatusResponse>, response: Response<TransactionStatusResponse>) {
                        if (response.isSuccessful) {
                            val queryResponse = response.body()
                            onResult(Result.success(queryResponse!!))
                        } else {
                            onResult(Result.failure(Exception("Query Transaction Failed")))
                        }
                    }

                    override fun onFailure(call: Call<TransactionStatusResponse>, t: Throwable) {
                        onResult(Result.failure(t))
                    }
                })
            }.onFailure {
                onResult(Result.failure(it))
            }
        }


    }

    override fun initializeMpesa(phoneNumber: String, amount: String): Flow<Resource<STKResponse>> {
        return callbackFlow {
            getAccessToken { result ->
                result.onSuccess { accessToken ->
                    val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                    val password = Base64.encodeToString(
                        (BuildConfig.MPESA_SHORTCODE + BuildConfig.MPESA_PASSKEY + timestamp).toByteArray(),
                        Base64.NO_WRAP
                    )

                    val stkPush = STKPush(
                        BusinessShortCode = BuildConfig.MPESA_SHORTCODE,
                        Password = password,
                        Timestamp = timestamp,
                        TransactionType = "CustomerPayBillOnline",
                        Amount = amount,
                        PartyA = phoneNumber,
                        PartyB = BuildConfig.MPESA_SHORTCODE,
                        PhoneNumber = phoneNumber,
                        CallBackURL = BuildConfig.MPESA_CALLBACK_URL,
                        AccountReference = BuildConfig.MPESA_ACCOUNT_REFERENCE,
                        TransactionDesc = "Test Payment"
                    )

                    val authHeader = "Bearer $accessToken"
                    val call = mpesaService.performSTKPush(authHeader, stkPush)
                    call.enqueue(object : Callback<STKResponse> {
                        override fun onResponse(
                            call: Call<STKResponse>,
                            response: Response<STKResponse>
                        ) {
                            if (response.isSuccessful) {
                                val stkResponse = response.body()
                                trySend(Resource.Success(stkResponse!!)).isSuccess
                            } else {
                                trySend(Resource.Error("STK Push Failed"))
                            }
                        }

                        override fun onFailure(call: Call<STKResponse>, t: Throwable) {
                            trySend(Resource.Error(t.message ?: "Failed to get access token"))
                        }
                    })
                }.onFailure {
                    trySend(Resource.Error(it.message ?: "Failed to get access token"))
                }
            }

            awaitClose { }
        }
    }


}
