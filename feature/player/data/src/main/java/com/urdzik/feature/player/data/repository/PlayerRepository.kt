package com.urdzik.feature.player.data.repository

import com.urdzik.feature.player.data.model.Book

interface PlayerRepository {
    suspend fun getBookById(id: String = "TGl0dGxlIFByaW5jZQ=="): Book
    suspend fun getLastChapterIdByBookId(id: String = "TGl0dGxlIFByaW5jZQ=="): String?
    suspend fun satLastChapterIdByBookId(id: String = "TGl0dGxlIFByaW5jZQ==", chapterId: String)
}
