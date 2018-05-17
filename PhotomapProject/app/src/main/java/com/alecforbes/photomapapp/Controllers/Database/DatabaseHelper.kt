package com.alecforbes.photomapapp.Controllers.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

/**
 * Created by Alec on 4/26/2018.
 */

class DatabaseHelper(context: Context):
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
        //private const val COLUMN_IMAGEURIS_ID = "uris_id"

        private const val TABLE_PHOTOMAPURIS = "photomapuris"
        private const val COLUMN_URI = "uris"
        private const val COLUMN_ASSOCIATED_MAP = "savedmap"

        private const val CREATE_PHOTOMAP_TABLE =
                "CREATE TABLE $TABLE_SAVEDPHOTOMAPS(" +
                "$COLUMN_MAP_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_MAPNAME TEXT)"
                     //   "$COLUMN_IMAGEURIS_ID INT, " +
                      //  "FOREIGN KEY($COLUMN_IMAGEURIS_ID) REFERENCES $TABLE_PHOTOMAPURIS (id))"

        private const val CREATE_URIS_TABLE =
                "CREATE TABLE $TABLE_PHOTOMAPURIS(" +
                        "$COLUMN_MAP_ID INTEGER PRIMARY KEY, " +
                        "$COLUMN_URI TEXT, $COLUMN_ASSOCIATED_MAP INTEGER)"

        private const val SQL_DELETE_PHOTOMAP_ENTRIES = "DROP TABLES IF EXISTS $TABLE_SAVEDPHOTOMAPS"
        private const val SQL_DELETE_URI_ENTRIES = "DROP TABLES IF EXISTS $TABLE_PHOTOMAPURIS"
    }

    // todo useful http://androidopentutorials.com/android-sqlite-join-multiple-tables-example/

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
        //photomapValues.put("_id", 0)
        photomapValues.put("mapname", mapName)
        //photomapValues.put("uris_id", 0)

        val newMapRowId = db.insert(TABLE_SAVEDPHOTOMAPS, null, photomapValues)

        imageUris.forEach {
            addURI(db, newMapRowId, it)
        }

    }

    fun addURI(db: SQLiteDatabase, newMapRowId: Long, uri: Uri) {

        val savedMapUris = ContentValues()

        //savedMapUris.put("_id", 0)
        savedMapUris.put("savedmap", newMapRowId)
        savedMapUris.put("uris", uri.toString())

        val newMapUrisId = db.insert(TABLE_PHOTOMAPURIS, null, savedMapUris)

    }

    fun getSavedMap(){

    }

    fun getURIs(){

    }

}