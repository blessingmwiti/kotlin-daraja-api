package com.blessingmwiti.kotlindarajaapi.presentation.payment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun PaymentScreen(viewModel: MpesaViewModel = getViewModel()) {
    var checkoutRequestID by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = uiState.phoneNumber,
                onValueChange = viewModel::updatePhoneNumber,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (uiState.phoneNumber.isNotEmpty() && uiState.amount.isNotEmpty()) {
                    val formattedPhoneNumber = formatPhoneNumber(uiState.phoneNumber)
                    coroutineScope.launch {
                        viewModel.initiatePayment(formattedPhoneNumber, uiState.amount) { result ->
                            result.onSuccess { response ->
                                Toast.makeText(context, "Response: ${response.CustomerMessage}", Toast.LENGTH_LONG).show()
                            }.onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter both phone number and amount", Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("Pay")
            }



            Spacer(modifier = Modifier.height(32.dp))

            Text("Query Transaction", style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = checkoutRequestID,
                onValueChange = { checkoutRequestID = it },
                label = { Text("Checkout Request ID") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (checkoutRequestID.isNotEmpty()) {
                    coroutineScope.launch {
                        viewModel.queryTransaction(checkoutRequestID) { result ->
                            result.onSuccess { response ->
                                Toast.makeText(context, "Query Response: ${response.ResponseDescription}", Toast.LENGTH_LONG).show()
                            }.onFailure { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter a Checkout Request ID", Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("Query")
            }
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
