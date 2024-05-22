package com.blessingmwiti.kotlindarajaapi

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var accessToken: String
    private lateinit var etPhoneNumber: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnPay: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etAmount = findViewById(R.id.etAmount)
        btnPay = findViewById(R.id.btnPay)

        btnPay.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val amount = etAmount.text.toString().trim()

            if (phoneNumber.isNotEmpty() && amount.isNotEmpty()) {
                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                getAccessToken(formattedPhoneNumber, amount)
            } else {
                Toast.makeText(this, "Please enter both phone number and amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0")) {
            "254" + phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }

    private fun getAccessToken(phoneNumber: String, amount: String) {
        val appKey = BuildConfig.MPESA_APP_KEY
        val appSecret = BuildConfig.MPESA_APP_SECRET
        val authString = "$appKey:$appSecret"
        val authHeader = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)

        Log.d("MPESA", "Auth String: $authString")
        Log.d("MPESA", "Encoded Auth Header: $authHeader")

        val call = MpesaClient.mpesaService.getAccessToken(authHeader)
        call.enqueue(object : Callback<AccessToken> {
            override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                if (response.isSuccessful) {
                    accessToken = response.body()?.access_token ?: ""
                    performSTKPush(phoneNumber, amount)
                } else {
                    Log.e("MPESA", "Failed to get access token: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Failed to get access token", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                Log.e("MPESA", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performSTKPush(phoneNumber: String, amount: String) {
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
        val call = MpesaClient.mpesaService.performSTKPush(authHeader, stkPush)
        call.enqueue(object : Callback<STKResponse> {
            override fun onResponse(call: Call<STKResponse>, response: Response<STKResponse>) {
                if (response.isSuccessful) {
                    val stkResponse = response.body()
                    Log.d("MPESA", "STK Response: ${stkResponse?.CustomerMessage}")
                    Toast.makeText(this@MainActivity, "Response: ${stkResponse?.CustomerMessage}", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("MPESA", "STK Push Failed: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "STK Push Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<STKResponse>, t: Throwable) {
                Log.e("MPESA", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}