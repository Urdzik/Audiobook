package com.urdzik.feature.player.data.model

data class Book(
    val id: String = "",
    val cover: String = "",
    val name: String = "",
    val chapter: List<Chapter> = emptyList()
)

data class Chapter(
    val id: String = "",
    val title: String = "",
    val audioUrl: String = "",
    val duration: Int = 0
)
