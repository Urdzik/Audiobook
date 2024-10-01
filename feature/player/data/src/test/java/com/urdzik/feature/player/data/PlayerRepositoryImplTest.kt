package com.urdzik.feature.player.data

import com.urdzik.core.api.contract.FirebaseApi
import com.urdzik.core.api.contract.model.BookResponse
import com.urdzik.core.database.contract.PlayerDao
import com.urdzik.core.database.contract.PlayerDataEntity
import com.urdzik.feature.player.data.map.toBook
import com.urdzik.feature.player.data.repository.PlayerRepositoryImpl
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class PlayerRepositoryImplTest {

    @Mock
    private lateinit var firebaseApi: FirebaseApi

    @Mock
    private lateinit var playerDao: PlayerDao

    private lateinit var playerRepository: PlayerRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        playerRepository = PlayerRepositoryImpl(firebaseApi, playerDao)
    }

    @Test
    fun `getBookById should return book from FirebaseApi`(): Unit = runBlocking {
        val bookId = "book1"
        val bookResponse = BookResponse(id = bookId, cover = "cover", name = "Book Name")
        val expectedBook = bookResponse.toBook()

        `when`(firebaseApi.getBookById(bookId)).thenReturn(bookResponse)

        val result = playerRepository.getBookById(bookId)

        assertEquals(expectedBook, result)
        verify(firebaseApi).getBookById(bookId)
    }

    @Test
    fun `getLastChapterIdByBookId should return last chapter id from PlayerDao`(): Unit = runBlocking {
        val bookId = "book1"
        val playerData = PlayerDataEntity(id = bookId, currentPlayerAudionId = "chapter2")

        `when`(playerDao.getPlayerDataById(bookId)).thenReturn(playerData)

        val result = playerRepository.getLastChapterIdByBookId(bookId)

        assertEquals("chapter2", result)
        verify(playerDao).getPlayerDataById(bookId)
    }

    @Test
    fun `getLastChapterIdByBookId should return null when PlayerData is not found`(): Unit = runBlocking {
        val bookId = "book1"

        `when`(playerDao.getPlayerDataById(bookId)).thenReturn(null)

        val result = playerRepository.getLastChapterIdByBookId(bookId)

        assertEquals(null, result)
        verify(playerDao).getPlayerDataById(bookId)
    }

    @Test
    fun `satLastChapterIdByBookId should insert PlayerData into PlayerDao`() = runBlocking {
        val bookId = "book1"
        val chapterId = "chapter3"

        playerRepository.satLastChapterIdByBookId(bookId, chapterId)

        verify(playerDao).insertPlayerData(
            PlayerDataEntity(id = bookId, currentPlayerAudionId = chapterId)
        )
    }
}