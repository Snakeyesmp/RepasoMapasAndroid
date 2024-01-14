package com.example.repasomapasandroid

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity(), Comunicador {

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var toolbar: Toolbar

    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        // Restaurar datos desde el bundle si está presente
        savedInstanceState?.let {
            latitud = it.getDouble("latitud", 0.0)
            longitud = it.getDouble("longitud", 0.0)
        }

        // Agregar ambos fragmentos a sus respectivos contenedores solo si savedInstanceState es nulo
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentMapa, MapsFragment())
                .replace(R.id.fragmentDatos, FragmentDatos())
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Guardar valores de latitud y longitud en el bundle
        outState.putDouble("latitud", latitud)
        outState.putDouble("longitud", longitud)

        super.onSaveInstanceState(outState)
    }

    override fun enviarCoordenadas(latitud: Double, longitud: Double) {
        this.latitud = latitud
        this.longitud = longitud
        val fragmentoDatos =
            supportFragmentManager.findFragmentById(R.id.fragmentDatos) as FragmentDatos?
        fragmentoDatos?.actualizarTexto(latitud, longitud)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_database -> {
                // Borrar toda la base de datos
                databaseHelper.clearDatabase()
                true
            }

            R.id.action_exit -> {
                // Salir de la aplicación
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}

