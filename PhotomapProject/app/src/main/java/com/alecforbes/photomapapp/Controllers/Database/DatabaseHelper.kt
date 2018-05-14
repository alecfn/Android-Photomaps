package com.alecforbes.photomapapp.Controllers.Database

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

/**
 * Created by Alec on 4/26/2018.
 */

class DatabaseHelper(context: Context, name: String?,
                     factory: SQLiteDatabase.CursorFactory?,
                     version: Int):
                         SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){

    // todo, this should be simple enough, save the image date in sqlite either raw in rows, or as the objects themselves (preferrably)
    // todo, check if already exists, raise dialog if the data is to be overwritten etc.

    // todo michaels advice: probably just save the image URI in the SQlite database, then if the uri is missing, just say it was skipped when populating

    /**
     * Stored photomaps are characterised as a database containing a table with just the saved name
     * of the photomap, and a link (foreign key) to another table containing the list of URIs used.
     */
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "savedPhotomapsdb"
        val TABLE_SAVEDPHOTOMAPS = "savedphotomaps"

        val COLUMN_ID = "_id"
        val COLUMN_MAPNAME = "mapname"
        val COLUMN_IMAGEURIS_ID = "uris_id"

        val TABLE_PHOTOMAPURIS = "photomapuris"
        val COLUMN_URI = "uris"

        val CREATE_PHOTOMAP_TABLE =
                "CREATE TABLE $TABLE_SAVEDPHOTOMAPS(" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_MAPNAME TEXT, " +
                        "$COLUMN_IMAGEURIS_ID INT, " +
                        "FOREIGN KEY($COLUMN_IMAGEURIS_ID) REFERENCES $TABLE_PHOTOMAPURIS (id))"

        val CREATE_URIS_TABLE =
                "CREATE TABLE $TABLE_PHOTOMAPURIS(" +
                        "$COLUMN_ID INTEGER PRIMARY KEY, " +
                        "$COLUMN_URI TEXT)"

    }

    // todo useful http://androidopentutorials.com/android-sqlite-join-multiple-tables-example/

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //TODO
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






}