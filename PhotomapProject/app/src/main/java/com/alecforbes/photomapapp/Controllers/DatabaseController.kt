package com.alecforbes.photomapapp.Controllers

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Alec on 4/26/2018.
 */

class DatabaseController(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    // todo, this should be simple enough, save the image date in sqlite either raw in rows, or as the objects themselves (preferrably)
    // todo, check if already exists, raise dialog if the data is to be overwritten etc.

    // todo michaels advice: probably just save the image URI in the SQlite database, then if the uri is missing, just say it was skipped when populating





}