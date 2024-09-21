package com.mduranx64.kotlingames

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ChessViewModel: ViewModel() {
    val board by mutableStateOf(Board())
}