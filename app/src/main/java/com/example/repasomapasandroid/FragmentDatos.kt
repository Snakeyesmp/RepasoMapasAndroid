package com.example.repasomapasandroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView




/**
 * A simple [Fragment] subclass.
 * Use the [fragment_datos.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentDatos : Fragment() {
    private lateinit  var textViewLongitud: TextView
    private lateinit  var textViewLatitud: TextView
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_datos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewLongitud = view.findViewById(R.id.textView_Longitud)
        textViewLatitud = view.findViewById(R.id.textView_Latitud)

        // Restaurar valores de latitud y longitud desde savedInstanceState
        if (savedInstanceState != null) {
            latitud = savedInstanceState.getDouble("latitud", 0.0)
            longitud = savedInstanceState.getDouble("longitud", 0.0)
        }

        // Actualizar texto con los valores actuales
        actualizarTexto(latitud, longitud)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("latitud", latitud)
        outState.putDouble("longitud", longitud)
    }

    fun actualizarTexto(latitud: Double, longitud: Double) {
        this.latitud = latitud
        this.longitud = longitud
        textViewLatitud.text = latitud.toString()
        textViewLongitud.text = longitud.toString()
    }
}
