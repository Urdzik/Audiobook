package com.urdzik.feature.player.data.repository

import com.urdzik.core.api.contract.FirebaseApi
import com.urdzik.core.database.contract.PlayerDao
import com.urdzik.core.database.contract.PlayerDataEntity
import com.urdzik.feature.player.data.map.toBook
import com.urdzik.feature.player.data.model.Book

class PlayerRepositoryImpl(
    private val firebaseApi: FirebaseApi,
    private val playerDao: PlayerDao
) : PlayerRepository {

    override suspend fun getBookById(id: String): Book {
        val bookResponse = firebaseApi.getBookById(id)
        val book = bookResponse.toBook()
        return book
    }

    override suspend fun getLastChapterIdByBookId(id: String): String? {
        val currentPlayerAudionId = playerDao.getPlayerDataById(id)?.currentPlayerAudionId
        return currentPlayerAudionId
    }

    override suspend fun satLastChapterIdByBookId(id: String, chapterId: String) {
        playerDao.insertPlayerData(
            PlayerDataEntity(
                id = id,
                currentPlayerAudionId = chapterId
            )
        )
    }
}