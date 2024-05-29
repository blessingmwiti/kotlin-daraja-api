package com.blessingmwiti.kotlindarajaapi.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blessingmwiti.kotlindarajaapi.domain.MpesaRepository
import com.blessingmwiti.kotlindarajaapi.domain.STKResponse
import com.blessingmwiti.kotlindarajaapi.domain.TransactionStatusResponse
import com.blessingmwiti.kotlindarajaapi.presentation.payment.PaymentUiState
import com.blessingmwiti.kotlindarajaapi.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MpesaViewModel(private val repository: MpesaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun initiatePayment(phoneNumber: String, amount: String, onResult: (Result<STKResponse>) -> Unit) {
        viewModelScope.launch {
            repository.performSTKPush(phoneNumber, amount, onResult)
        }
    }

    fun initiatesPayment(phoneNumber: String, amount: String) {
        viewModelScope.launch {
            repository.initializeMpesa(phoneNumber, amount).collect { it ->
                when (it) {
                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            loading = true
                        )
                    }
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            stkPushResponse = it.data
                        )
                    }
                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            error = it.message ?: "Unknown Error Occurred"
                        )
                    }
                }
            }
        }
    }

    fun queryTransaction(checkoutRequestID: String, onResult: (Result<TransactionStatusResponse>) -> Unit) {
        viewModelScope.launch {
            repository.queryTransaction(checkoutRequestID, onResult)
        }
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.update { it.copy(phoneNumber = phoneNumber) }
    }

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }
}
