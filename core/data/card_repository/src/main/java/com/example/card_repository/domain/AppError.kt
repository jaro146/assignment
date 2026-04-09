package com.example.card_repository.domain

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
data class AppError(
    val message: String,
    val cause: Throwable? = null
)