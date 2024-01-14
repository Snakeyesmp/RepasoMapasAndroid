package com.example.repasomapasandroid

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LocationDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_LOCATIONS = "locations"
        private const val KEY_ID = "id"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createLocationsTable = ("CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL" + ")")
        db.execSQL(createLocationsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    fun addLocation(latitude: Double, longitude: Double): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_LATITUDE, latitude)
        contentValues.put(KEY_LONGITUDE, longitude)
        val success = db.insert(TABLE_LOCATIONS, null, contentValues)
        db.close()
        return success
    }

    fun getAllLocations(): ArrayList<Location> {
        val locationsList = ArrayList<Location>()
        val selectQuery = "SELECT  * FROM $TABLE_LOCATIONS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var latitude: Double
        var longitude: Double

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(KEY_ID)
                val latitudeIndex = cursor.getColumnIndex(KEY_LATITUDE)
                val longitudeIndex = cursor.getColumnIndex(KEY_LONGITUDE)

                if (idIndex != -1 && latitudeIndex != -1 && longitudeIndex != -1) {
                    id = cursor.getInt(idIndex)
                    latitude = cursor.getDouble(latitudeIndex)
                    longitude = cursor.getDouble(longitudeIndex)

                    val location = Location(id = id, latitude = latitude, longitude = longitude)
                    locationsList.add(location)
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return locationsList
    }

    fun printAllLocations() {
        val locations = getAllLocations()
        for (location in locations) {
            Log.d("DatabaseHelper", "ID: ${location.id}, Latitud: ${location.latitude}, Longitud: ${location.longitude}")
        }
    }

    fun clearDatabase() {
        val db = this.writableDatabase
        db.delete(TABLE_LOCATIONS, null, null)
        db.close()
    }

}