package com.example.repasomapasandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentDatos : Fragment() {
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var listViewLocations: ListView
    private lateinit var locationsArray: Array<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_datos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            latitud = savedInstanceState.getDouble("latitud", 0.0)
            longitud = savedInstanceState.getDouble("longitud", 0.0)
        }

        actualizarTexto(latitud, longitud)

        databaseHelper = DatabaseHelper(requireContext())
        listViewLocations = view.findViewById(R.id.listView_locations)

        val locations = databaseHelper.getAllLocations()
        locationsArray =
            Array(locations.size) { i -> "ID: ${locations[i].id}, Latitud: ${locations[i].latitude}, Longitud: ${locations[i].longitude}" }
        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, locationsArray)
        listViewLocations.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("latitud", latitud)
        outState.putDouble("longitud", longitud)
    }

    fun actualizarTexto(latitud: Double, longitud: Double) {
        this.latitud = latitud
        this.longitud = longitud
    }

    fun updateLocations() {
        val locations = databaseHelper.getAllLocations()
        locationsArray =
            Array(locations.size) { i -> "ID: ${locations[i].id}, Latitud: ${locations[i].latitude}, Longitud: ${locations[i].longitude}" }
        adapter.clear()
        adapter.addAll(locationsArray.toList())
        adapter.notifyDataSetChanged()
    }
}