package com.example.card_repository.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.card_repository.model.ScratchCardEntity


@Database(entities = [ScratchCardEntity::class], version = 1, exportSchema = false)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}