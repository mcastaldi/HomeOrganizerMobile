package castaldini.homeorganizationmobile.database;

/**
 * Class that holds the string variables used to interact with the Rooms Table
 */

public class StoragesTable {
    static final String TABLE_NAME = "storagesTable";
    static final String COLUMN_KEY = "storageKey";
    public static final String COLUMN_ID = "storageId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_ROOM = "roomId";

    static final String [] ALL_COLUMNS =
            {COLUMN_KEY,COLUMN_ID,COLUMN_NAME,COLUMN_COST,COLUMN_ROOM};
    static final String SQL_CREATE=
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_KEY + " INTEGER PRIMARY KEY, " +
                    COLUMN_ID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_COST + " INTEGER, " +
                    COLUMN_ROOM + " INTEGER NOT NULL" + ");";

    static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME + ";";
}
