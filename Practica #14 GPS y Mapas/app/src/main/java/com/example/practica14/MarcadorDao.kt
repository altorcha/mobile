package com.example.practica14

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarcadorDao {
    @Query("SELECT * FROM marcadores")
    fun obtenerMarcadores(): Flow<List<MarcadorEntity>>

    @Insert
    suspend fun insertarMarcador(marcador: MarcadorEntity)
}