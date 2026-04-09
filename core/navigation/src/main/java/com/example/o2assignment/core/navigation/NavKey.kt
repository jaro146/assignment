package com.example.o2assignment.core.navigation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object HomeNavKey : NavKey

@Serializable
data class ScratchNavKey(
    val cardId: Int
) : NavKey

@Serializable
@Stable
@Immutable
data class ActivateNav(
    val cardId: Int
) : NavKey