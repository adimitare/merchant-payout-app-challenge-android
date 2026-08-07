package com.example.androidinterview.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidinterview.domain.usecase.GetMerchantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMerchantUseCase: GetMerchantUseCase
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    init {
        loadMerchant()
    }

    fun loadMerchant() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            runCatching {
                getMerchantUseCase()
            }.onSuccess {
                _uiState.value =
                    HomeUiState.Success(it)
            }.onFailure {
                _uiState.value =
                    HomeUiState.Error(
                        it.message ?: "Unknown error"
                )
            }
        }
    }
}