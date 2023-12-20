package com.example.unidad8practica1.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.unidad8practica1.Comunidad

class ComunidadDAO {
    fun cargarLista(context: Context?): MutableList<Comunidad> {
        lateinit var res: MutableList<Comunidad>
        lateinit var cursor: Cursor
        try {
            val db = DBopenHelper.getInstance(context)!!.readableDatabase
            val sql = "SELECT * FROM comunidades;"
            cursor = db.rawQuery(sql, null)
            val columnas = arrayOf(
                ComunidadContract.Companion.Entrada.COLUMNA_ID,
                ComunidadContract.Companion.Entrada.COLUMNA_NOMBRE,
                ComunidadContract.Companion.Entrada.COLUMNA_IMAGEN,
                ComunidadContract.Companion.Entrada.COLUMNA_HABITANTES,
                ComunidadContract.Companion.Entrada.COLUMNA_CAPITAL,
                ComunidadContract.Companion.Entrada.COLUMNA_LATITUD,
                ComunidadContract.Companion.Entrada.COLUMNA_LONGITUD,
                ComunidadContract.Companion.Entrada.COLUMNA_ICONO
            )

            cursor = db.query(
                ComunidadContract.Companion.Entrada.NOMBRE_TABLA,
                columnas, null, null, null, null, null
            )
            res = mutableListOf()

            while (cursor.moveToNext()) {
                val nueva = Comunidad(
                    cursor.getInt(0), cursor.getString(1),
                    cursor.getInt(2), cursor.getInt(3),
                    cursor.getString(4), cursor.getDouble(5),
                    cursor.getDouble(6), cursor.getInt(7)
                )
                res.add(nueva)
            }
        } finally {
            cursor.close()
        }
        return res
    }

    fun eliminarComunidad(context: Context, comunidades: Comunidad) {
        val db = DBopenHelper.getInstance(context)?.writableDatabase

        if (db != null) {
            try {
                // Definir la condición para la eliminación
                val whereClause = "${ComunidadContract.Companion.Entrada.COLUMNA_ID} = ?"
                val whereArgs = arrayOf(comunidades.id.toString())

                // Realizar la eliminación en la base de datos
                db.delete(
                    ComunidadContract.Companion.Entrada.NOMBRE_TABLA,
                    whereClause,
                    whereArgs
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.close()
            }
        }
    }

    fun actualizarNombre(context: Context, comunidad: Comunidad, nuevoNombre: String) {
        val db = DBopenHelper.getInstance(context)?.writableDatabase

        if (db != null) {
            try {
                db.beginTransaction() // Inicia una transacción
                val values = ContentValues()
                values.put(ComunidadContract.Companion.Entrada.COLUMNA_NOMBRE, nuevoNombre)

                // Actualiza el nombre de la comunidad en la base de datos
                val whereClause = "${ComunidadContract.Companion.Entrada.COLUMNA_ID} = ?"
                val whereArgs = arrayOf(comunidad.id.toString())
                db.update(
                    ComunidadContract.Companion.Entrada.NOMBRE_TABLA,
                    values,
                    whereClause,
                    whereArgs
                )
                db.setTransactionSuccessful() // Marca la transacción como exitosa
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction() // Finaliza la transacción
                db.close()
            }
        }
    }
}