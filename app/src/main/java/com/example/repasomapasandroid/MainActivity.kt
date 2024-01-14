package com.example.repasomapasandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), Comunicador {

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val fragmentoDatos = supportFragmentManager.findFragmentById(R.id.fragmentDatos) as FragmentDatos?
        fragmentoDatos?.actualizarTexto(latitud, longitud)
    }

}

