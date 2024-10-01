package com.urdzik.core.api.contract.model

import kotlinx.serialization.Serializable

@Serializable
data class ChapterResponse(
    val id: String = "",
    val title: String = "",
    val audioUrl: String = "",
    val duration: Int = 0
)
