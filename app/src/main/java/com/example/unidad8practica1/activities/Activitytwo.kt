package com.example.unidad8practica1.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.unidad8practica1.R

class Activitytwo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dos)

        val nombreComunidad = intent.getStringExtra("comunidadNombre")
        val imagenBandera = intent.getIntExtra("imagenBandera", 0)
        val posicion = intent.getIntExtra("position", 0)

        val imagen = findViewById<ImageView>(R.id.imageView)
        imagen.setImageResource(imagenBandera)

        val edit = findViewById<EditText>(R.id.editText)
        edit.hint = nombreComunidad

        val btnGuardarCambios = findViewById<Button>(R.id.buttoncambiar)
        btnGuardarCambios.setOnClickListener{
            val nuevoNombre = edit.text.toString()
            val comunidad = lista[posicion]

            //Actualiza el nombre en la base de datos
            DAO.actualizarNombre(this, comunidad, nuevoNombre)

            val returnIntent = Intent()
            returnIntent.putExtra("nuevoNombre", nuevoNombre)
            returnIntent.putExtra("position", posicion)

            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        val btnCancelar = findViewById<Button>(R.id.buttoncancelar)
        btnCancelar.setOnClickListener {
            finish()
        }
    }
}