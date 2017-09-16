package castaldini.homeorganizationmobile.database;

/**
 * Class that holds the string variables used to interact with the Items Table
 */

public class ItemsTable {
    static final String TABLE_NAME = "itemsTable";
    static final String COLUMN_KEY = "itemKey";
    public static final String COLUMN_ID = "itemId";
    public static final String COLUMN_NAME = "itemName";
    public static final String COLUMN_COST = "cost";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_BARCODE = "barcode";
    public static final String COLUMN_ROOM = "roomId";
    public static final String COLUMN_STORAGE = "storageId";

    static final String[] ALL_COLUMNS =
            {COLUMN_KEY,COLUMN_ID,COLUMN_NAME,COLUMN_COST,COLUMN_COUNT, COLUMN_BARCODE,COLUMN_ROOM,COLUMN_STORAGE};
    static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COLUMN_KEY + " INTEGER PRIMARY KEY, " +
                    COLUMN_ID + " INTEGER, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_COST + " INTEGER, " +
                    COLUMN_COUNT + " REAL, " +
                    COLUMN_BARCODE + " INTEGER, "+
                    COLUMN_ROOM + " INTEGER, " +
                    COLUMN_STORAGE + " INTEGER" + ");";

    static final String SQL_DELETE =
            "DROP TABLE " + TABLE_NAME + ";";
}
