package com.example.navigationflow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.navigationflow.databinding.ActivityPantallaUnoBinding

class PantallaUno : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaUnoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPantallaUnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSiguiente.setOnClickListener {
            val nombre = binding.editNombre.text.toString()
            val intent = Intent(this, PantallaDos::class.java)
            intent.putExtra("NOMBRE", nombre)
            startActivity(intent)
        }
    }
}
