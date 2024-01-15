package com.example.repasomapasandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : SupportMapFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Notifica cuando el mapa esté listo
        getMapAsync(this)

        databaseHelper = DatabaseHelper(requireContext())

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configuración del mapa
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // CARGAR DE LA BBDD LOS MARCADORES
        val locations = databaseHelper.getAllLocations()
        for (location in locations) {
            val latLng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title("Marcador en destino"))
        }

        // OnMapClickListener
        mMap.setOnMapClickListener { latLng ->
            Toast.makeText(
                requireContext(),
                "Latitud: ${latLng.latitude}, Longitud: ${latLng.longitude}",
                Toast.LENGTH_LONG
            ).show()

            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Marcador en destino")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(true) // Hacer el marcador arrastrable
            )

            // Guardar en la BBDD
            databaseHelper.addLocation(latLng.latitude, latLng.longitude)
        }

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                // Opcional: hacer algo cuando se empieza a arrastrar el marcador
            }

            override fun onMarkerDrag(marker: Marker) {
                // Opcional: hacer algo mientras se está arrastrando el marcador
            }

            override fun onMarkerDragEnd(marker: Marker) {
                // Actualizar las coordenadas del marcador en la base de datos
                val latLng = marker.position
                databaseHelper.updateLocation(marker.id, latLng.latitude, latLng.longitude)
            }
        })

        // OnMapLongClickListener
        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Marcador largo")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .snippet("Teléfono: 983989784")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            (activity as? Comunicador)?.enviarCoordenadas(latLng.latitude, latLng.longitude)

            // Guardar en la BBDD
            databaseHelper.addLocation(latLng.latitude, latLng.longitude)

        }

    }


}
