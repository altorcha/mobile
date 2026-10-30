package com.example.miniproyecto03

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PublicacionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).publicacionDao()
    val publicaciones: Flow<List<Publicacion>> = dao.obtenerTodas()

    fun insertarPublicacion(titulo: String, descripcion: String, fotoUri: String, fecha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertar(
                Publicacion(
                    titulo = titulo,
                    descripcion = descripcion,
                    fotoUri = fotoUri,
                    fecha = fecha
                )
            )
        }
    }

    fun eliminarPublicacion(publicacion: Publicacion) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.eliminar(publicacion)
        }
    }
}
