package castaldini.homeorganizationmobile.database;

/**
 * Class that holds the string variables used to interact with the Rooms Table
 */

public class RoomsTable {
    static final String TABLE_NAME = "roomsTable";
    static final String COLUMN_KEY = "roomKey";
    public static final String COLUMN_ID = "roomId";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FLOOR = "cost";

    static final String[] ALL_COLUMNS =
            {COLUMN_KEY,COLUMN_ID,COLUMN_NAME,COLUMN_FLOOR};
    static final String SQL_CREATE=
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_KEY + " INTEGER PRIMARY KEY, " +
                    COLUMN_ID + " INTEGER NOT NULL, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_FLOOR + " TEXT" + ");";

    static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME + ";";
}
