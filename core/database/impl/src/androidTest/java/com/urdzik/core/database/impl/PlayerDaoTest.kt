package com.urdzik.core.database.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.urdzik.core.database.contract.PlayerDao
import com.urdzik.core.database.contract.PlayerDataEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class PlayerDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var playerDao: PlayerDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        playerDao = db.playerDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndGetPlayerDataById() = runBlocking {
        // Given
        val playerData = PlayerDataEntity(id = "1", currentPlayerAudionId = "chapter1")

        // When
        playerDao.insertPlayerData(playerData)
        val result = playerDao.getPlayerDataById("1")

        // Then
        assertEquals(playerData, result)
    }

    @Test
    fun updatePlayerData() = runBlocking {
        // Given
        val playerData = PlayerDataEntity(id = "1", currentPlayerAudionId = "chapter1")
        playerDao.insertPlayerData(playerData)

        // Update player data
        val updatedPlayerData = PlayerDataEntity(id = "1", currentPlayerAudionId = "chapter2")
        playerDao.updatePlayerData(updatedPlayerData)

        // When
        val result = playerDao.getPlayerDataById("1")

        // Then
        assertEquals(updatedPlayerData, result)
    }

    @Test
    fun deletePlayerData() = runBlocking {
        // Given
        val playerData = PlayerDataEntity(id = "1", currentPlayerAudionId = "chapter1")
        playerDao.insertPlayerData(playerData)

        // When
        playerDao.deletePlayerData(playerData)
        val result = playerDao.getPlayerDataById("1")

        // Then
        assertNull(result)
    }
}