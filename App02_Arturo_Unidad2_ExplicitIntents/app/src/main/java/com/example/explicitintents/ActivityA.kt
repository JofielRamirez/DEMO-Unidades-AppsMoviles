package com.example.explicitintents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.explicitintents.databinding.ActivityABinding

class ActivityA : AppCompatActivity() {

    private lateinit var binding: ActivityABinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityABinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIniciar.setOnClickListener {
            val usuario = binding.editUsuario.text.toString()
            val intent = Intent(this, ActivityB::class.java)
            intent.putExtra("USUARIO", usuario)
            startActivity(intent)
        }
    }
}
