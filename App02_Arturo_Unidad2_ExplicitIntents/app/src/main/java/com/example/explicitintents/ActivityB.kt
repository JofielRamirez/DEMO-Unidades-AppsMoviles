package com.example.explicitintents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.explicitintents.databinding.ActivityBBinding

class ActivityB : AppCompatActivity() {

    private lateinit var binding: ActivityBBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuario = intent.getStringExtra("USUARIO") ?: "Usuario"
        binding.tvBienvenido.text = "Bienvenido, $usuario"
    }
}
