package com.urdzik.core.api.contract.model

import kotlinx.serialization.Serializable

@Serializable
data class BookResponce(
    val id: String = "",
    val cover: String = "",
    val name: String = "",
    val chapter: List<Chapter> = emptyList()
)