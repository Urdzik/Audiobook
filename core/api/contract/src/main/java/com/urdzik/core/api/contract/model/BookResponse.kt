package com.urdzik.core.api.contract.model

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val id: String = "",
    val cover: String = "",
    val name: String = "",
    val chapter: List<ChapterResponse> = emptyList()
)