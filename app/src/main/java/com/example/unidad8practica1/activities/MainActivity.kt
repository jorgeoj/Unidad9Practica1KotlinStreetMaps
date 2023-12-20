package com.example.unidad8practica1.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unidad8practica1.Comunidad
import com.example.unidad8practica1.R
import com.example.unidad8practica1.adapter.ComunidadAdapter
import com.example.unidad8practica1.database.ComunidadDAO
import com.example.unidad8practica1.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView


private lateinit var adapter: ComunidadAdapter
private lateinit var layoutManager: LinearLayoutManager
internal lateinit var lista:MutableList<Comunidad>
private lateinit var intentLaunch: ActivityResultLauncher<Intent>
internal lateinit var DAO : ComunidadDAO
private var comunidadPosition = -1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                finish()
            }
        })

        setSupportActionBar(binding.toolbar)
        DAO = ComunidadDAO()
        lista = DAO.cargarLista(this)
        layoutManager = LinearLayoutManager(this)
        binding.rvComunidades.layoutManager = layoutManager
        adapter = ComunidadAdapter(lista) { Comunidad ->
            onItemSelected(Comunidad)
        }

        binding.rvComunidades.adapter = adapter

        binding.rvComunidades.setHasFixedSize(true)
        binding.rvComunidades.setOnCreateContextMenuListener(this)
        registerForContextMenu(binding.rvComunidades)

        intentLaunch = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                result : androidx.activity.result.ActivityResult ->
            if (result.resultCode == RESULT_OK){
                val posicion = result.data?.extras?.getInt("position", 0)
                lista[posicion!!].nombre=result.data?.extras?.getString("nuevoNombre").toString()
                adapter.notifyItemChanged(posicion)

            }
        }
    }

    private fun onItemSelected(comunidad : Comunidad) {
        Toast.makeText(this,"Yo soy de ${comunidad.nombre}", Toast.LENGTH_SHORT).show()
        // Crear un Intent para iniciar la actividad MapaActivity
        val intent = Intent(this, MapaActivity::class.java)

        intent.putExtra("comunidadNombre", comunidad.nombre)
        intent.putExtra("comunidadHabitantes", comunidad.habitantes)
        intent.putExtra("comunidadCapital", comunidad.capital)
        intent.putExtra("comunidadLatitud", comunidad.latitud)
        intent.putExtra("comunidadLongitud", comunidad.longitud)

        // Iniciar la actividad MapaActivity
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reload -> {
                lista.clear()
                //Esto hace que vuelvan pero si le ponemos la linea de abajo despues de listaAll ya no vuelve porque recarga de la base de datos
                lista.addAll(DAO.cargarLista(this))
                adapter.notifyDataSetChanged()
                return true
            }

            R.id.menu_clear -> {
                lista.clear()
                adapter.notifyDataSetChanged()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        var comunidadEliminada: Comunidad
        comunidadEliminada = lista[item.groupId]
        when (item.itemId) {
            R.id.context_edit -> {
                comunidadPosition = item.groupId // Almacena la posición de la comunidad editada
                val intent = Intent(this, Activitytwo::class.java)
                val comunidad = lista[comunidadPosition]
                intent.putExtra("comunidadNombre", comunidad.nombre)
                intent.putExtra("position", item.groupId)
                intent.putExtra("imagenBandera", comunidad.imagen)
                intentLaunch.launch(intent)
                return true
            }

            R.id.context_delete -> {
                val alert =
                    AlertDialog.Builder(this).setTitle("Eliminar ${comunidadEliminada.nombre}")
                        .setMessage(
                            "¿Estas seguro de que desea eliminar ${comunidadEliminada.nombre}?"
                        )
                        .setNeutralButton("Cerrar", null).setPositiveButton("Aceptar")
                        { _, _ ->
                            //Para eliminar de la base de datos
                            DAO.eliminarComunidad(this, comunidadEliminada)
                            lista.remove(comunidadEliminada)
                            adapter.notifyItemRemoved(item.groupId)
                            adapter.notifyItemRangeChanged(item.groupId, lista.size)
                            display("Se ha eliminado ${comunidadEliminada.nombre}")
                            comunidadPosition = -1

                        }.create()
                alert.show()
            }
            else -> return super.onContextItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val nuevoNombre = data?.getStringExtra("nuevoNombre")

                if (comunidadPosition != -1 && nuevoNombre != null) {
                    val comunidad = lista[comunidadPosition]  // Obtén la comunidad de la lista
                    DAO.actualizarNombre(this, comunidad, nuevoNombre) //Con esto cambia el nombre en la base de datos
                    adapter.notifyItemChanged(comunidadPosition) // Notifica al adaptador del cambio
                }
            }
        }
    }

    private fun display(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}