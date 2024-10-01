package com.urdzik.core.database.contract

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "player_data",
)
data class PlayerDataEntity(
    @PrimaryKey val id: String,
    val currentPlayerAudionId: String,
)
