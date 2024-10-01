package com.urdzik.core.database.contract

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerData(playerData: PlayerDataEntity)

    @Update
    suspend fun updatePlayerData(playerData: PlayerDataEntity)

    @Query("SELECT * FROM player_data WHERE id = :id")
    suspend fun getPlayerDataById(id: String): PlayerDataEntity?

    @Delete
    suspend fun deletePlayerData(playerData: PlayerDataEntity)
}