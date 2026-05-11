package com.example.statepersistence

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainViewModel(private val state: SavedStateHandle) : ViewModel() {

    var nombre: String
        get() = state["nombre"] ?: ""
        set(value) { state["nombre"] = value }

    var recordar: Boolean
        get() = state["recordar"] ?: false
        set(value) { state["recordar"] = value }
}
