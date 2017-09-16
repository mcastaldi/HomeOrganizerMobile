package castaldini.homeorganizationmobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  DBHelped class that deals with creating, updating, or downgrading the database version
 */

class DBHelper extends SQLiteOpenHelper {

    private static final String DB_FILE_NAME = "homeOrganization.db";
    private static final int DB_VERSION = 2;

    DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemsTable.SQL_CREATE);
        db.execSQL(StoragesTable.SQL_CREATE);
        db.execSQL(RoomsTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Clear tables and start over
        db.execSQL(ItemsTable.SQL_DELETE);
        db.execSQL(StoragesTable.SQL_DELETE);
        db.execSQL(RoomsTable.SQL_DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        try{
            db.execSQL(ItemsTable.SQL_DELETE);
        } catch (SQLiteException e){
            e.printStackTrace();
        }try{
            db.execSQL(StoragesTable.SQL_DELETE);
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        try{
            db.execSQL(RoomsTable.SQL_DELETE);
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        onCreate(db);
    }
}
