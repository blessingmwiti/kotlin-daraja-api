package com.blessingmwiti.kotlindarajaapi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import com.blessingmwiti.kotlindarajaapi.presentation.payment.PaymentScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface {
                PaymentScreen()
            }
        }
    }
}