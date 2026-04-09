package com.example.card_repository.model

import com.example.card_repository.domain.AppError

data class CardActionResult(
    val error: AppError? = null,
    val code: String? = null,
    val activationResult: Boolean? = null
)
