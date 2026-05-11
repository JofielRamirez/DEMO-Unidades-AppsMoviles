package com.example.statepersistence

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.statepersistence.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editNombre.setText(viewModel.nombre)
        binding.checkRecordar.isChecked = viewModel.recordar
        mostrarDatos()

        binding.btnGuardar.setOnClickListener {
            viewModel.nombre = binding.editNombre.text.toString()
            viewModel.recordar = binding.checkRecordar.isChecked
            mostrarDatos()
        }
    }

    private fun mostrarDatos() {
        binding.tvResultado.text = "Nombre: ${viewModel.nombre}\nRecordar: ${viewModel.recordar}"
    }
}
