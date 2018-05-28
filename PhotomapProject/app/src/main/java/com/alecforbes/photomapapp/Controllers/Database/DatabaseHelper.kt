package com.alecforbes.photomapapp.Controllers.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.io.File

/**
 * Created by Alec on 4/26/2018.
 */

class DatabaseHelper(val context: Context):
                         SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    // todo, this should be simple enough, save the image date in sqlite either raw in rows, or as the objects themselves (preferrably)
    // todo, check if already exists, raise dialog if the data is to be overwritten etc.

    // todo michaels advice: probably just save the image URI in the SQlite database, then if the uri is missing, just say it was skipped when populating

    /**
     * Stored photomaps are characterised as a database containing a table with just the saved name
     * of the photomap, and a link (foreign key) to another table containing the list of URIs used.
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
            val GET_ROWID_SQL = "SELECT _id FROM $TABLE_SAVEDPHOTOMAPS WHERE mapname='$mapName';"
            val cursor = db.rawQuery(GET_ROWID_SQL, null)
            if (cursor != null && cursor.moveToFirst()) {
                val oldMapRowId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))

                imageUris.forEach {
                    addURI(db, oldMapRowId, mapName, it)
                }

            }
            cursor.close()

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

        // If the URI doesn't exist in the DB, add it otherwise ignore it
        val GET_URI_SQL = "SELECT uris FROM $TABLE_PHOTOMAPURIS WHERE uris='$uri' AND $COLUMN_ASSOCIATED_MAP='$mapName';"

        val cursor = db.rawQuery(GET_URI_SQL, null)

        if (cursor.moveToFirst()){  // If the cursor can move, that uri is already stored
            cursor.close()
            return
        }

        // To save the new image copy, get the path and append the mapname a copy to be unique

        val filename = File(uri.path).name + "_" + mapName + "_copy" //fixme check exists
        val fileContents = context.contentResolver.openInputStream(uri)

        val fileBytes = fileContents.readBytes()


        var newUri: Uri? = null
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileBytes)
            val newFile = File(context.filesDir, filename) // Application data directory
            newUri = Uri.fromFile(newFile)
            it.close()
        }

        savedMapUri.put("savedmap", mapRowId)
        savedMapUri.put("uris", newUri.toString())


        db.insert(TABLE_PHOTOMAPURIS, null, savedMapUri)

    }

    fun getSavedMaps(): ArrayList<String> {

        val savedMaps = ArrayList<String>()

        val SELECT_ALL_MAPS = "SELECT * FROM $TABLE_SAVEDPHOTOMAPS"

        val db = this.readableDatabase
        val cursor = db.rawQuery(SELECT_ALL_MAPS, null)

        while(cursor.moveToNext()){
            val mapName = cursor.getString(cursor.getColumnIndex(COLUMN_MAPNAME))
            savedMaps.add(mapName)

        }

        cursor.close()
        return savedMaps

    }

    fun getSavedMapUris(savedMapName: String): ArrayList<Uri>{

        val savedUris = ArrayList<Uri>()

        // Get the ID index for the map, then look for uris with that value
        val GET_ROWID_SQL = "SELECT * FROM $TABLE_SAVEDPHOTOMAPS WHERE mapname='$savedMapName';"


        val db = this.readableDatabase

        val mapTableCursor = db.rawQuery(GET_ROWID_SQL, null)

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

    fun getURIs(){

    }

    fun deleteMap(){
        // todo
    }

    private fun checkMapExists(db: SQLiteDatabase, mapName: String): Boolean {
        val FIND_MAP_SQL = "SELECT mapname FROM $TABLE_SAVEDPHOTOMAPS WHERE mapname='$mapName';"

        val cursor = db.rawQuery(FIND_MAP_SQL, null)

        if (cursor.count > 0) {
            cursor.close()
            return true
        } else
            cursor.close()
            return false
    }

}