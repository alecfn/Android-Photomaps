package com.alecforbes.photomapapp.Controllers.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import java.io.File

/**
 * Created by Alec on 4/26/2018.
 * This database helper class handles all SQL queries and persistence functions to the database
 * of saved photomaps.
 *
 * Image URIs are stored in a table with an associated map in another table, the ID of which
 * acts as the primary key.
 */

class DatabaseHelper(val context: Context):
                         SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    /**
     * Stored photomaps are characterised as a database containing a table with just the saved name
     * of the photomap, and a link (foreign key) to another table containing the list of URIs used.
     *
     * The following companion objects define relevant constants such as column and table names,
     * and SQL statements.
     */
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "savedPhotomapsdb"
        private const val TABLE_SAVEDPHOTOMAPS = "savedphotomaps"

        private const val COLUMN_MAP_ID = "_id"
        private const val COLUMN_MAPNAME = "mapname"

        private const val TABLE_PHOTOMAPURIS = "photomapuris"
        private const val COLUMN_URI = "uris"
        private const val COLUMN_ASSOCIATED_MAP = "savedmap"

        private const val CREATE_PHOTOMAP_TABLE =
                "CREATE TABLE $TABLE_SAVEDPHOTOMAPS(" +
                "$COLUMN_MAP_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_MAPNAME TEXT)"

        private const val CREATE_URIS_TABLE =
                "CREATE TABLE $TABLE_PHOTOMAPURIS(" +
                        "$COLUMN_MAP_ID INTEGER PRIMARY KEY, " +
                        "$COLUMN_URI TEXT, $COLUMN_ASSOCIATED_MAP INTEGER)"

        private const val SQL_DELETE_PHOTOMAP_ENTRIES = "DROP TABLES IF EXISTS $TABLE_SAVEDPHOTOMAPS"
        private const val SQL_DELETE_URI_ENTRIES = "DROP TABLES IF EXISTS $TABLE_PHOTOMAPURIS"
    }


    /**
     * Supporting functions necessary for initialisation of SQLite database in Android.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_URI_ENTRIES)
        db.execSQL(SQL_DELETE_PHOTOMAP_ENTRIES)
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_PHOTOMAP_TABLE)
        db.execSQL(CREATE_URIS_TABLE)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly){
            // Must enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }

    /**
     * Add a map to the maps table when the user clicks save.
     */
    fun addMap(mapName: String, imageUris: ArrayList<Uri>){
        val db = writableDatabase
        val photomapValues = ContentValues()

        photomapValues.put("mapname", mapName)


        if (!checkMapExists(db, mapName)) {
            val newMapRowId = db.insert(TABLE_SAVEDPHOTOMAPS, null, photomapValues)

            imageUris.forEach {
                addURI(db, newMapRowId, mapName, it)
            }

        } else {
            // Map already exists, so just add any new URI values

            val oldMapRowId = getMapRowId(mapName, db)

                imageUris.forEach {
                    addURI(db, oldMapRowId!!, mapName, it)
                }

            }

        }



    /**
     * When the user saves a map, save the URI into a new local file accessible to to application.
     *
     * This has to be done as the Application does not have access to the files stored in other
     * applications (documents, gallery etc.) due to Android security sandboxing. The only other
     * way to access images would be through an intent, which can't be done when loading a saved
     * map.
     */
    private fun addURI(db: SQLiteDatabase, mapRowId: Long, mapName: String, uri: Uri) {

        val savedMapUri = ContentValues()
        val savedMapId = getMapRowId(mapName, this.readableDatabase)

        // To save the new image copy, get the path and append the mapname a copy to be unique

        val filename = File(uri.path).name + "_" + mapName + "_copy" //fixme check exists
        val fileContents = context.contentResolver.openInputStream(uri)

        val fileBytes = fileContents.readBytes()

        val newFile = File(context.filesDir, filename) // Application data directory
        val newUri = Uri.fromFile(newFile) // Need the new uri to check exists as that is stored

        // If the URI doesn't exist in the DB, add it otherwise ignore it
        val GET_URI_SQL = "SELECT uris FROM $TABLE_PHOTOMAPURIS WHERE uris='$newUri' AND $COLUMN_ASSOCIATED_MAP='$savedMapId';"

        val cursor = db.rawQuery(GET_URI_SQL, null)

        if (cursor.moveToFirst()){
            // If the cursor can move, that uri is already stored so return
            cursor.close()
            return
        }

        // File didn't exist, write it to the data directory
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileBytes)
            it.close()
        }

        savedMapUri.put("savedmap", mapRowId)
        savedMapUri.put("uris", newUri.toString())


        db.insert(TABLE_PHOTOMAPURIS, null, savedMapUri)

    }

    /**
     * Get all the saved maps in the database instance.
     */
    fun getSavedMaps(): ArrayList<String> {

        val savedMaps = ArrayList<String>()

        val selectAllMapsSQL = "SELECT * FROM $TABLE_SAVEDPHOTOMAPS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(selectAllMapsSQL, null)

        while(cursor.moveToNext()){
            val mapName = cursor.getString(cursor.getColumnIndex(COLUMN_MAPNAME))
            savedMaps.add(mapName)

        }

        cursor.close()
        return savedMaps

    }

    /**
     * Get the URIs saved in the URI table where the map id the one identifying the map.
     */
    fun getSavedMapUris(savedMapName: String): ArrayList<Uri>{

        val savedUris = ArrayList<Uri>()

        // Get the ID index for the map, then look for uris with that value
        val getRowIdSQL = "SELECT * FROM $TABLE_SAVEDPHOTOMAPS WHERE $COLUMN_MAPNAME='$savedMapName';"

        val db = this.readableDatabase

        val mapTableCursor = db.rawQuery(getRowIdSQL, null)

        mapTableCursor.moveToFirst()
        val associatedMapId = mapTableCursor.getLong(mapTableCursor.getColumnIndexOrThrow("_id"))
        mapTableCursor.close()

        val SELECT_ALL_URIS_SQL = "SELECT * FROM $TABLE_PHOTOMAPURIS WHERE $COLUMN_ASSOCIATED_MAP='$associatedMapId';"
        val uriTableCursor = db.rawQuery(SELECT_ALL_URIS_SQL, null)

        while (uriTableCursor.moveToNext()){

            val uri = uriTableCursor.getString(uriTableCursor.getColumnIndex(COLUMN_URI))
            savedUris.add(Uri.parse(uri))

        }

        uriTableCursor.close()
        return savedUris

    }

    /**
     * Get the row ID used to identify a map. This is used to identify the map that a URI is saved
     * in, and is unique to that map. It is quite useful to have, so this function retrieves it.
     */
    private fun getMapRowId(mapName: String, db: SQLiteDatabase): Long? {
        val getRowIDSQL = "SELECT _id FROM $TABLE_SAVEDPHOTOMAPS WHERE mapname='$mapName';"

        val cursor = db.rawQuery(getRowIDSQL, null)
        var mapRowId: Long? = null
        if (cursor != null && cursor.moveToFirst()) {
            mapRowId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            }
        cursor.close()

        return mapRowId
    }

    /**
     * Delete an entry from both the Map table and the URI table when the user clicks delete.
     */
    fun deleteMap(savedMapName: String){


        // Get the _id of the map in the savedmap table to use to delete uri entries
        val savedMapId = getMapRowId(savedMapName, this.readableDatabase)
        // Drop the entry for the map in the maps table, and the entries with URIs in URI table

        val dropMapSQL = "DELETE FROM $TABLE_SAVEDPHOTOMAPS WHERE $COLUMN_MAPNAME='$savedMapName';"
        val dropMapUrisSQL = "DELETE FROM $TABLE_PHOTOMAPURIS WHERE $COLUMN_ASSOCIATED_MAP='$savedMapId';"

        // Delete file copies as well used to save the map

        val getUriSQL = "SELECT uris FROM $TABLE_PHOTOMAPURIS WHERE $COLUMN_ASSOCIATED_MAP='$savedMapId';"
        val db = this.writableDatabase

        // Now drop the table entries

        val savedFilesCursor = db.rawQuery(getUriSQL, null)

        if (savedFilesCursor.count > 0){
            while(savedFilesCursor.moveToNext()) {
                // Get all the uris in the table and delete the files
                val fileUri = savedFilesCursor.getString(savedFilesCursor.getColumnIndex("uris"))
                val savedFile = File(fileUri.toString())
                savedFile.delete()

            }
        }else{
            Log.e("No file uris", "No file URIs were found for $savedMapName")
        }

        savedFilesCursor.close()

        db.execSQL(dropMapUrisSQL)
        db.execSQL(dropMapSQL)

    }

    /**
     * Update a map already in the database with new URI entries.
     */
    fun updateMap(savedMapName: String, imageUris: ArrayList<Uri>){

        val oldMapId = getMapRowId(savedMapName, this.readableDatabase)

        imageUris.forEach {uri ->
            addURI(this.writableDatabase, oldMapId!!, savedMapName, uri)
        }
    }

    /**
     * Supporting function to find if a map already exists, so they can be overwritten.
     */
    fun checkMapExists(db: SQLiteDatabase, mapName: String): Boolean {
        val findMapUri = "SELECT mapname FROM $TABLE_SAVEDPHOTOMAPS WHERE mapname='$mapName';"

        val cursor = db.rawQuery(findMapUri, null)

        if (cursor.count > 0) {
            cursor.close()
            return true
        } else
            cursor.close()
            return false
    }

}