package com.the42studios.floodhazard.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.the42studios.floodhazard.entity.Location
import com.the42studios.floodhazard.entity.Notification
import com.the42studios.floodhazard.entity.Sensitivity
import com.the42studios.floodhazard.entity.Settings
import java.util.*

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DatabaseHandler.DB_NAME, null, DatabaseHandler.DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.i("CREATE DB", "=============")
        val CREATE_LOCATION_TABLE = "CREATE TABLE $LOCATION_TABLE_NAME ($ID INTEGER PRIMARY KEY, $NAME TEXT,$STATUS TEXT,$DETAILS TEXT);"
        db.execSQL(CREATE_LOCATION_TABLE)
        val CREATE_NOTIFICATION_TABLE = "CREATE TABLE $NOTIFICATION_TABLE_NAME($ID INTEGER PRIMARY KEY, $SCHEDULED INTEGER);"
        db.execSQL(CREATE_NOTIFICATION_TABLE)
        val CREATE_SENSITIVITY_TABLE = "CREATE TABLE $SENSITIVITY_TABLE_NAME ($ID INTEGER PRIMARY KEY, $LOCATION TEXT, $INTENSITY TEXT, $DATE TEXT, $HOUR INTEGER, $SENSITIVITY_LEVEL TEXT, $SENSITIVITY_DETAIL TEXT);"
        db.execSQL(CREATE_SENSITIVITY_TABLE)
        val CREATE_SETTINGS_TABLE = "CREATE TABLE $SETTING_TABLE_NAME ($URL TEXT );"
        db.execSQL(CREATE_SETTINGS_TABLE)

/*        val INSERT_LOCATION_SAMPLE = "INSERT INTO $LOCATION_TABLE_NAME ($ID, $NAME, $STATUS, $DETAILS) VALUES (1,'SAMPLE_LOCATION_N','N', 'Sample Details for Location with NO');"
        db.execSQL(INSERT_LOCATION_SAMPLE)
        val INSERT_LOCATION_SAMPLE1 = "INSERT INTO $LOCATION_TABLE_NAME ($ID, $NAME, $STATUS, $DETAILS) VALUES (2,'SAMPLE_LOCATION_Y','Y', 'Sample Details for Location with YES');"
        db.execSQL(INSERT_LOCATION_SAMPLE1)

        val INSERT_SENSITIVITY_TABLE = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (1, 'SAMPLE_LOCATION_12', '2.2', '2018-07-29', 12, 'GREEN', 'Moderate rainfall');"
        db.execSQL(INSERT_SENSITIVITY_TABLE)
        val INSERT_SENSITIVITY_TABLE1 = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (2, 'SAMPLE_LOCATION_13', '5.2', '2018-07-29', 13, 'YELLOW', 'Below knee flood in 4 hours');"
        db.execSQL(INSERT_SENSITIVITY_TABLE1)
        val INSERT_SENSITIVITY_TABLE2 = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (3, 'SAMPLE_LOCATION_14', '7.2', '2018-07-29', 14, 'ORANGE', 'Not passable by small vehicles and not passable by children and Adult');"
        db.execSQL(INSERT_SENSITIVITY_TABLE2)
        val INSERT_SENSITIVITY_TABLE3 = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (4, 'SAMPLE_LOCATION_11', '8.2', '2018-07-29', 11, 'RED', 'Not passable by small vehicles and not passable by children and Adult and Violent flood if P rate is above 30 in 4 cosecutive hours');"
        db.execSQL(INSERT_SENSITIVITY_TABLE3)
        val INSERT_SENSITIVITY_TABLE4 = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (5, 'SAMPLE_LOCATION_15', '8.2', '2018-07-29', 15, 'RED', 'Not passable by small vehicles and not passable by children and Adult and Violent flood if P rate is above 30 in 4 cosecutive hours');"
        db.execSQL(INSERT_SENSITIVITY_TABLE4)
        val INSERT_SENSITIVITY_TABLE5 = "INSERT INTO $SENSITIVITY_TABLE_NAME ($ID, $LOCATION, $INTENSITY, $DATE, $HOUR, $SENSITIVITY_LEVEL, $SENSITIVITY_DETAIL) VALUES (6, 'SAMPLE_LOCATION_10', '8.2', '2018-07-29', 10, 'RED', 'Not passable by small vehicles and not passable by children and Adult and Violent flood if P rate is above 30 in 4 cosecutive hours');"
        db.execSQL(INSERT_SENSITIVITY_TABLE5)*/
        Log.i("END OF CREATE DB", "=============")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.i("UPDATE DB", "=============")
        val DROP_LOCATION_TABLE = "DROP TABLE IF EXISTS $LOCATION_TABLE_NAME"
        db.execSQL(DROP_LOCATION_TABLE)
        val DROP_NOTIFICATION_TABLE = "DROP TABLE IF EXISTS $NOTIFICATION_TABLE_NAME"
        db.execSQL(DROP_NOTIFICATION_TABLE)
        val DROP_SENSITIVITY_TABLE = "DROP TABLE IF EXISTS $SENSITIVITY_TABLE_NAME"
        db.execSQL(DROP_SENSITIVITY_TABLE)
        val DROP_SETTINGS_TABLE = "DROP TABLE IF EXISTS $SETTING_TABLE_NAME"
        db.execSQL(DROP_SETTINGS_TABLE)
        onCreate(db)

        Log.i("END OF UPDATE DB", "=============")
    }

    fun deleteAllSensitivity(_date: String) {
        Log.i("DELETE SENSITIVITY","Date : ${_date}")
        val db = this.writableDatabase
        db.delete(SENSITIVITY_TABLE_NAME, null, null)
        db.close()
    }

    fun getSensitivityByDateAndHours(_date: String, _startHour: Int, _endHour: Int, isNotification: Boolean): List<Sensitivity> {
        Log.i("TASK PARAM: "+_date,  " ====== getSensitivityByDateAndHours =======");
        val sensitivityList = ArrayList<Sensitivity>()
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $SENSITIVITY_TABLE_NAME " +
                "WHERE ($HOUR >= $_startHour AND $HOUR <= $_endHour) AND $DATE = '$_date'"
        val selectQueryPerHour =
                "SELECT  st.* FROM Sensitivity st " +
//                "SELECT  * FROM $SENSITIVITY_TABLE_NAME " +
                "INNER JOIN Location lt " +
                "ON lt.name = st.location " +
//                "WHERE ($HOUR >= $_startHour AND $HOUR <= $_endHour) AND $DATE = '$_date' " +
                "WHERE (st.hour >= $_startHour AND st.hour <= $_endHour) AND st.date = '$_date' " +
//                "AND $LOCATION IN (SELECT * FROM Location WHERE $STATUS = 'Y')"
                "AND lt.status = 'Y'"
        var query: String?
        if(isNotification) {
            query = selectQueryPerHour
        } else {
            query = selectQuery
        }
        Log.i("QUERY TASK ", query);
        val cursor = db.rawQuery(query, null)
        if (cursor != null) {
             Log.d("CURSOR RESULT", "================")
            if (cursor.moveToFirst()) {
                do {
                    Log.d("CURSOR NEXT", "================"+Arrays.deepToString(cursor.columnNames) + "==="+cursor.columnCount)
                    val sensitivity = Sensitivity()
                    sensitivity.id = cursor.getInt(cursor.getColumnIndex(ID))
                    sensitivity.location = cursor.getString(cursor.getColumnIndex(LOCATION))
                    sensitivity.intensity = cursor.getString(cursor.getColumnIndex(INTENSITY))
                    sensitivity.date = cursor.getString(cursor.getColumnIndex(DATE))
                    sensitivity.hour = cursor.getInt(cursor.getColumnIndex(HOUR))
                    sensitivity.sensitivityLevel = cursor.getString(cursor.getColumnIndex(SENSITIVITY_LEVEL))
                    sensitivity.sensitivityDetail = cursor.getString(cursor.getColumnIndex(SENSITIVITY_DETAIL))
                    Log.i("CURSOR RESULT", sensitivity.toString())
                    sensitivityList.add(sensitivity)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        Log.d("NOTIF RESULT", "================"+Arrays.deepToString(sensitivityList.toTypedArray()))

        return sensitivityList
    }

    fun addSchedule() : Boolean {
        Log.i("SAVE","NEW SCHEDULE")
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(SCHEDULED, 1)
        val _success = db.insert(NOTIFICATION_TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun addSensitivity(sensitivity: Sensitivity): Boolean {
        Log.i("SAVE","NEW SENSITIVITY")
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(LOCATION, sensitivity.location)
        values.put(INTENSITY, sensitivity.intensity)
        values.put(DATE, sensitivity.date)
        values.put(HOUR, sensitivity.hour)
        values.put(SENSITIVITY_LEVEL, sensitivity.sensitivityLevel)
        values.put(SENSITIVITY_DETAIL, sensitivity.sensitivityDetail)
        val _success = db.insert(SENSITIVITY_TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun addLocation(location: Location): Boolean {
        Log.i("SAVE","NEW LOCATION")
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(NAME, location.name)
        values.put(DETAILS, location.details)
        values.put(STATUS, location.status)
        val _success = db.insert(LOCATION_TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }

    fun updateLocation(location: Location): Boolean {
        Log.i("UPDATE LOCATION", "Parameter ${location}")
        val db = this.writableDatabase
        val values = ContentValues()
        val _success : Long
        if(location.id == 0) {
            Log.i("UPDATE","WHEN ID IS NULL ==== ")
            var ediLoc = getLocationByName(location.name)
            if(ediLoc == null) {
                var res = addLocation(location)
               if(res)
                   _success = 1
                else
                   _success = -1
            } else {
                values.put(ID, ediLoc.id)
                values.put(STATUS, ediLoc.status)
                values.put(NAME, location.name)
                values.put(DETAILS, location.details)
                _success = db.update(LOCATION_TABLE_NAME, values, ID + "=?", arrayOf(ediLoc.id.toString())).toLong()
            }
        } else {
            Log.i("UPDATE","STATUS ==== " )
            values.put(STATUS, location.status)
            _success = db.update(LOCATION_TABLE_NAME, values, ID + "=?", arrayOf(location.id.toString())).toLong()
        }
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun getLocationList(): List<Location> {
        val locationList = ArrayList<Location>()
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $LOCATION_TABLE_NAME"
        Log.i("QUERY LOCATION", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val location = Location()
                    location.id = cursor.getInt(cursor.getColumnIndex(ID))
                    location.name = cursor.getString(cursor.getColumnIndex(NAME))
                    location.status = cursor.getString(cursor.getColumnIndex(STATUS))
                    location.details = cursor.getString(cursor.getColumnIndex(DETAILS))
                    locationList.add(location)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return locationList
    }

    fun getScheduleList(): List<Notification> {
        val notificationList = ArrayList<Notification>()
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $NOTIFICATION_TABLE_NAME"
        Log.i("QUERY NOTIFICATION", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val notification = Notification()
                    notification.id = cursor.getInt(cursor.getColumnIndex(ID))
                    notification.isScheduled = cursor.getInt(cursor.getColumnIndex(SCHEDULED))
                    notificationList.add(notification)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return notificationList
    }

    fun deleteLocation(_id: Int): Boolean {
        val db = this.writableDatabase
        val _success = db.delete(LOCATION_TABLE_NAME, ID + "=?", arrayOf(_id.toString())).toLong()
        db.close()
        return Integer.parseInt("$_success") != -1
    }

    fun deleteSettings() {
        Log.i("DELETE SETTINGS","START")
        val db = this.writableDatabase
        db.delete(SETTING_TABLE_NAME, null,null)
        db.close()
    }

    fun getSettings() : String {
        Log.i("GET SETTINGS","START")
        var settings : String = ""
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $SETTING_TABLE_NAME"
        Log.i("QUERY SETTINGS", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                settings = cursor.getString(cursor.getColumnIndex(URL))
            }
        }
        Log.i("RETURN SETTINGS", settings);
        cursor.close()
        db.close()
        return settings
    }

    fun addSettings(settings: Settings): Boolean {
        Log.i("SAVE","NEW SETTINGS FOR URL")
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(URL, settings.url)
        val _success = db.insert(SETTING_TABLE_NAME, null, values)
        db.close()
        Log.v("InsertedId", "$_success")
        return (Integer.parseInt("$_success") != -1)
    }


    fun getNotify(): List<Location> {
        val locationList = ArrayList<Location>()
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $LOCATION_TABLE_NAME WHERE $STATUS = 'Y'"
        Log.i("QUERY LOCATION WITH YES", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val location = Location()
                    location.id = cursor.getInt(cursor.getColumnIndex(ID))
                    location.name = cursor.getString(cursor.getColumnIndex(NAME))
                    location.status = cursor.getString(cursor.getColumnIndex(STATUS))
                    location.details = cursor.getString(cursor.getColumnIndex(DETAILS))
                    locationList.add(location)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()

        db.close()
        return locationList
    }

    fun getLocationList(_id: Int): Location {
        val location = Location()
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $LOCATION_TABLE_NAME WHERE $ID = $_id"
        Log.i("QUERY LOCATION", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                    location.id = cursor.getInt(cursor.getColumnIndex(ID))
                    location.name = cursor.getString(cursor.getColumnIndex(NAME))
                    location.status = cursor.getString(cursor.getColumnIndex(STATUS))
                    location.details = cursor.getString(cursor.getColumnIndex(DETAILS))
            }
        }
        Log.i("RETURN LOCATION", location.toString());
        cursor.close()
        db.close()
        return location
    }

    fun getLocationByName(_name: String): Location? {

        val db = writableDatabase
        val selectQuery = "SELECT  * FROM $LOCATION_TABLE_NAME WHERE $NAME = '$_name'"
        Log.i("QUERY LOCATION", selectQuery);
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor != null && cursor.moveToFirst()) {
            val location = Location()
            location.id = cursor.getInt(cursor.getColumnIndex(ID))
            location.name = cursor.getString(cursor.getColumnIndex(NAME))
            location.status = cursor.getString(cursor.getColumnIndex(STATUS))
            location.details = cursor.getString(cursor.getColumnIndex(DETAILS))
            cursor.close()
            Log.i("GET LOCATION", location.toString());
            return location
        }
        return null
    }

    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "FloodHazard"
        private val LOCATION_TABLE_NAME = "Location"
        private val NOTIFICATION_TABLE_NAME = "Notification"
        private val ID = "Id"
        private val SCHEDULED = "isScheduled"
        private val NAME = "Name"
        private val LOCATION = "Location"
        private val INTENSITY = "Intensity"
        private val STATUS = "Status"
        private val DETAILS = "Details"
        private val DATE = "Date"
        private val HOUR = "Hour"
        private val SENSITIVITY_LEVEL = "SensitivityLevel"
        private val SENSITIVITY_DETAIL = "SensitivityDetails"
        private val SENSITIVITY_TABLE_NAME = "Sensitivity"
        private val SETTING_TABLE_NAME = "Settings"
        private val URL = "Url"
    }
}