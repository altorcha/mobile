package com.example.miniproyecto02

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY fechaCreacion DESC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Insert
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query("DELETE FROM tareas WHERE completada = 1")
    suspend fun eliminarCompletadas(): Int
}
