package com.example.androidinterview.ui.home

import androidx.lifecycle.ViewModel
import com.example.androidinterview.domain.repository.MerchantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MerchantRepository
): ViewModel()