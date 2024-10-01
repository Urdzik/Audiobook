package com.urdzik.core.database.impl

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.urdzik.core.database.contract.PlayerDao
import com.urdzik.core.database.contract.PlayerDataEntity
import org.koin.dsl.module

@Database(
    entities = [PlayerDataEntity::class],
    version = 2,
    exportSchema = false,
)

internal abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}

val databaseModule = module {

    // Provide AppDatabase instance
    single {
        Room.databaseBuilder(
            get(), // Koin provides the ApplicationContext
            AppDatabase::class.java,
            "database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Provide PlayerDao instance
    single {
        get<AppDatabase>().playerDao()
    }
}