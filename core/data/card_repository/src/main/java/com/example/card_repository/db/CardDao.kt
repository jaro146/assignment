package com.example.card_repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.card_repository.model.CardState
import com.example.card_repository.model.ScratchCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert
    suspend fun insert(card: ScratchCardEntity)

    @Update
    suspend fun update(card: ScratchCardEntity)

    @Query("SELECT * FROM cards ORDER BY id ASC")
    fun getAllCards(): Flow<List<ScratchCardEntity>>

    @Query("SELECT * FROM cards WHERE id = :cardId")
    fun getCardById(cardId: Int): Flow<ScratchCardEntity?>

    @Query("DELETE FROM cards WHERE id = :cardId")
    suspend fun deleteById(cardId: Int)

    @Query("UPDATE cards SET cardState = :newStatus WHERE id = :cardId")
    suspend fun updateCardStatus(cardId: Int, newStatus: CardState)
}