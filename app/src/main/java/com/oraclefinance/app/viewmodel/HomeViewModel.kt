package com.oraclefinance.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oraclefinance.app.data.dto.DashboardSummary
import com.oraclefinance.app.data.dto.NewTransactionRequest
import com.oraclefinance.app.data.repository.AuthRepository
import com.oraclefinance.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val summary: DashboardSummary? = null,
    val aiText: String? = null,
    val loadingSummary: Boolean = false,
    val loadingAI: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val financeRepository: FinanceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loadingSummary = true, error = null)
            try {
                val summary = financeRepository.getDashboard()
                _state.value = _state.value.copy(summary = summary, loadingSummary = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, loadingSummary = false)
            }
        }
    }

    fun analyzeWithAI() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loadingAI = true, aiText = null)
            try {
                val resp = financeRepository.analyze()
                _state.value = _state.value.copy(aiText = resp.text, loadingAI = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message, loadingAI = false)
            }
        }
    }

    fun addTransaction(amount: Double, description: String, type: String, category: String) {
        viewModelScope.launch {
            try {
                financeRepository.addTransaction(
                    NewTransactionRequest(amount, description, type, category)
                )
                loadDashboard()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}

