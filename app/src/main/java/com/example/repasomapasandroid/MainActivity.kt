package com.example.repasomapasandroid

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.Uri
import android.os.BatteryManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class MainActivity : AppCompatActivity(), Comunicador {

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private lateinit var toolbar: Toolbar

    // PARA PODER TOMAR FOTOS
    private lateinit var takePicturePreviewLauncher: ActivityResultLauncher<Void?>

    // PARA EL NIVEL DE BATERIA
    private val batteryLevelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level: Int = intent.getIntExtra("level", -1)
            Toast.makeText(context, "Nivel de batería: $level%", Toast.LENGTH_LONG).show()
            // Anular el registro del BroadcastReceiver después de recibir el nivel de batería
            unregisterReceiver(this)
        }
    }

    // PARA LA BASE DE DATOS
    private lateinit var databaseHelper: DatabaseHelper

    // PARA EL PERMISO DE LA CAMARA
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takePicturePreviewLauncher.launch(null)
        } else {
            Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_LONG).show()
        }
    }


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


        takePicturePreviewLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            // Crear un intent para DisplayImageActivity
            val intent = Intent(this, DisplayImageActivity::class.java)
            // Convertir el Bitmap a bytes y ponerlo en el intent
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val byteArray = stream.toByteArray()
            intent.putExtra("image", byteArray)
            // Iniciar DisplayImageActivity
            startActivity(intent)
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
            R.id.action_open_camera -> {
                // Comprobar si ya tenemos el permiso de la cámara
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Si tenemos permiso, lanzamos la cámara
                        takePicturePreviewLauncher.launch(null)
                        true
                    }


                    else -> {
                        // Si no, solicitamos el permiso
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        true
                    }
                }
            }

            R.id.action_show_battery -> {

                registerReceiver(batteryLevelReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

                // Obtener el nivel de batería actual
                val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                    applicationContext.registerReceiver(null, ifilter)
                }
                val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                // Mostrar el nivel de batería en un Toast
                Toast.makeText(this, "Nivel de batería: $level%", Toast.LENGTH_LONG).show()
                true
            }
            // LO UNICO QUE SE ABRE EL CHROME POR DEFECTO
            R.id.action_open_web -> {
                // Crear un intent para abrir un navegador web
                val webpage = Uri.parse("http://www.google.es")
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                // Crear un chooser para que el usuario seleccione qué navegador usar
                val chooser = Intent.createChooser(intent, "Elige un navegador")
                // Iniciar el chooser
                startActivity(chooser)
                true
            }
            R.id.action_open_prime_fragment -> {
                // Reemplazar el fragmento actual con PrimeFragment
                reemplazarFragmento(PrimeFragment())
                true
            }
            R.id.action_open_data_fragment -> {
                // Reemplazar el fragmento actual con FragmentDatos
                reemplazarFragmento(FragmentDatos())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /* OTRA MANERA DE HACER EL CHOOSER
    R.id.action_open_web -> {
            // Crear un intent para abrir un navegador web
            val webpage = Uri.parse("http://www.google.es")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            // Añadir la categoría DEFAULT al intent
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            // Crear un chooser para que el usuario seleccione qué navegador usar
            val chooser = Intent.createChooser(intent, "Elige un navegador")
            // Iniciar el chooser
            startActivity(chooser)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
     */


    /**
     * FUNCION PARA REEMPLAZAR FRAGMENTOS
     */
    private fun reemplazarFragmento(fragmento: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Reemplaza el fragmento actual con el nuevo
        transaction.replace(R.id.fragmentDatos, fragmento)

        // Puedes agregar la transacción a la pila para permitir retroceder
        transaction.addToBackStack(null)

        // Realiza la transacción
        transaction.commit()
    }

}

