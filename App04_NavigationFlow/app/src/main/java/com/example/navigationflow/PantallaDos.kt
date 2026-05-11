package com.example.navigationflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationflow.databinding.ActivityPantallaDosBinding

class PantallaDos : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaDosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaDosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra("NOMBRE") ?: "desconocido"
        binding.tvMensaje.text = "Hola, $nombre! Has llegado a la Pantalla 2."

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}
