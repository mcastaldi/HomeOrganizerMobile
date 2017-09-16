package castaldini.homeorganizationmobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.model.House;
import castaldini.homeorganizationmobile.model.Storage;
import castaldini.homeorganizationmobile.model.Item;
import castaldini.homeorganizationmobile.model.Room;

/**
 * DataSource class used as intermediary for interaction between application and the SQLite database
 */

public class DataSource {
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mDbHelper;

    public DataSource(Context context){

        mDbHelper = new DBHelper(context);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void open(){ mDatabase = mDbHelper.getWritableDatabase(); }

    public void close(){ mDbHelper.close(); }

    //All creation methods insert their respective entity into the database then return it
    public Storage createStorage(Storage storage){
        ContentValues values = storage.toValues();
        mDatabase.insert(StoragesTable.TABLE_NAME,null,values);
        return storage;
    }

    public Item createItem(Item item){
        ContentValues values = item.toValues();
        mDatabase.insert(ItemsTable.TABLE_NAME,null,values);
        return item;
    }

    public Room createRoom(Room room){
        ContentValues values = room.toValues();
        mDatabase.insert(RoomsTable.TABLE_NAME,null,values);
        return room;
    }

    //This method loads all information of the house and returns a list of all the rooms.
    public List<Room> loadRooms(){
        List<Room> rooms = new ArrayList<>();
        Cursor roomCursor = mDatabase.query(RoomsTable.TABLE_NAME,RoomsTable.ALL_COLUMNS,null,null,null,null,RoomsTable.COLUMN_ID);
        while(roomCursor.moveToNext()){
            Room room = new Room();
            room.setPrimaryKey(roomCursor.getInt(roomCursor.getColumnIndex(RoomsTable.COLUMN_KEY)));
            room.setId(roomCursor.getInt(roomCursor.getColumnIndex(RoomsTable.COLUMN_ID)));
            room.setName(roomCursor.getString(roomCursor.getColumnIndex(RoomsTable.COLUMN_NAME)));
            room.setFloor(roomCursor.getString(roomCursor.getColumnIndex(RoomsTable.COLUMN_FLOOR)));
            String[] idsForStorage = {Integer.toString(room.getId())};
            Cursor storageCursor = mDatabase.query(StoragesTable.TABLE_NAME,StoragesTable.ALL_COLUMNS,StoragesTable.COLUMN_ROOM + "=?",idsForStorage,null,null,StoragesTable.COLUMN_ID);
            List<Storage> storages = new ArrayList<>();
            while(storageCursor.moveToNext()){
                Storage storage = new Storage();
                storage.setPrimaryKey(storageCursor.getInt(storageCursor.getColumnIndex(StoragesTable.COLUMN_KEY)));
                storage.setId(storageCursor.getInt(storageCursor.getColumnIndex(StoragesTable.COLUMN_ID)));
                storage.setName(storageCursor.getString(storageCursor.getColumnIndex(StoragesTable.COLUMN_NAME)));
                storage.setCost(storageCursor.getInt(storageCursor.getColumnIndex(StoragesTable.COLUMN_COST)));
                storage.setRoomId(room.getId());
                storage.setRoomName(room.getName());
                String[] idsForItem = {Integer.toString(room.getId()),Integer.toString(storage.getId())};
                Cursor itemCursor = mDatabase.query(ItemsTable.TABLE_NAME,ItemsTable.ALL_COLUMNS,ItemsTable.COLUMN_ROOM + "=? and " + ItemsTable.COLUMN_STORAGE + "=?",idsForItem,null,null,ItemsTable.COLUMN_ID);
                List<Item> items = new ArrayList<>();
                while(itemCursor.moveToNext()){
                    Item item = new Item();
                    item.setPrimaryKey(itemCursor.getInt(itemCursor.getColumnIndex(ItemsTable.COLUMN_KEY)));
                    item.setId(itemCursor.getInt(itemCursor.getColumnIndex(ItemsTable.COLUMN_ID)));
                    item.setBarcode(itemCursor.getInt(itemCursor.getColumnIndex(ItemsTable.COLUMN_BARCODE)));
                    item.setName(itemCursor.getString(itemCursor.getColumnIndex(ItemsTable.COLUMN_NAME)));
                    item.setCost(itemCursor.getInt(itemCursor.getColumnIndex(ItemsTable.COLUMN_COST)));
                    item.setCount(itemCursor.getInt(itemCursor.getColumnIndex(ItemsTable.COLUMN_COUNT)));
                    item.setRoomId(room.getId());
                    item.setStorageId(storage.getId());
                    items.add(item);
                }
                itemCursor.close();
                storage.setItems(items);
                storages.add(storage);
            }
            storageCursor.close();
            room.setStorages(storages);
            rooms.add(room);
        }
        roomCursor.close();
        return rooms;
    }

    //Simple function that calls the previous function and returns in as a house object
    public House loadHouse(){
        return new House(loadRooms());
    }

    //methods to could the number of entities in the database
    public long getRoomCount(){
        return DatabaseUtils.queryNumEntries(mDatabase, RoomsTable.TABLE_NAME);
    }

    private long getStorageCount(){
        return DatabaseUtils.queryNumEntries(mDatabase, StoragesTable.TABLE_NAME);
    }

    private long getItemCount(){
        return DatabaseUtils.queryNumEntries(mDatabase, ItemsTable.TABLE_NAME);
    }

    public int getNumStoragesInRoom(int roomId){
        String[] ids = {Integer.toString(roomId)};
        Cursor cursor = mDatabase.query(StoragesTable.TABLE_NAME,StoragesTable.ALL_COLUMNS,StoragesTable.COLUMN_ROOM + "=?",ids,null,null,null);
        int numStorages = cursor.getCount();
        cursor.close();
        return numStorages;
    }

    public List<Storage> getStoragesInRoom(int roomId){
        String[] ids = {Integer.toString(roomId)};
        Cursor cursor = mDatabase.query(StoragesTable.TABLE_NAME,StoragesTable.ALL_COLUMNS,StoragesTable.COLUMN_ROOM + "=?",ids,null,null,StoragesTable.COLUMN_ID);
        List<Storage> storages = new ArrayList<>();
        while (cursor.moveToNext()) {
            Storage s = new Storage();
            s.setId(cursor.getInt(cursor.getColumnIndex(StoragesTable.COLUMN_ID)));
            s.setCost(cursor.getInt(cursor.getColumnIndex(StoragesTable.COLUMN_COST)));
            s.setName(cursor.getString(cursor.getColumnIndex(StoragesTable.COLUMN_NAME)));
            s.setRoomId(roomId);
            storages.add(s);
        }
        cursor.close();
        return storages;
    }

    public int getNumItemsInStorage(int roomId, int storageId){
        String[] ids = {Integer.toString(roomId),Integer.toString(storageId)};
        Cursor cursor = mDatabase.query(ItemsTable.TABLE_NAME,ItemsTable.ALL_COLUMNS,ItemsTable.COLUMN_ROOM + "=? and " + ItemsTable.COLUMN_STORAGE + "=?",ids,null,null,null);
        int numItems = cursor.getCount();
        cursor.close();
        return numItems;
    }

    //Seeding methods are used on the first run of the application to give some demo information.
    public void seedRooms(){
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("first","Living room",0));
        rooms.add(new Room("first","Kitchen",1));
        rooms.add(new Room("first","Sunroom",2));
        rooms.add(new Room("second","Library",3));
        rooms.add(new Room("second","Bedroom",4));

        long numRooms = getRoomCount();
        if(numRooms == 0){
            for ( Room r: rooms){
                try{
                    createRoom(r);
                } catch (SQLiteException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void seedStorages(){
        List<Storage> storages = new ArrayList<>();
        storages.add(new Storage("Cabinet",0,50000,0));
        storages.add(new Storage("Desk",1,45000,0));
        storages.add(new Storage("Cupboard",0,42000,1));
        storages.add(new Storage("Fridge",1,52343,1));
        storages.add(new Storage("Shelves",0,23400,2));
        storages.add(new Storage("Desk",1,14200,2));
        storages.add(new Storage("Bookshelf",0,88800,3));
        storages.add(new Storage("Dresser",0,56700,4));

        long numStorages = getStorageCount();
        if(numStorages == 0){
            for ( Storage s: storages){
                try{
                    createStorage(s);
                } catch (SQLiteException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void seedItems(){
        List<Item> items = new ArrayList<>();
        items.add(new Item("Batteries",0,200,5,1111111,0,0));
        items.add(new Item("Computer",0,130000,1,2222222,0,1));
        items.add(new Item("Cereal",0,800,3,3333333,1,0));
        items.add(new Item("Milk",0,450,2,4444444,1,1));
        items.add(new Item("Star Wars Book",0,700,10,5555555,2,0));
        items.add(new Item("Plants",0,3580,1,6666666,2,1));
        items.add(new Item("More Plants",1,3580,4,7777777,2,1));
        items.add(new Item("Episode 1",0,3780,1,8888888,3,0));
        items.add(new Item("Episode 2",1,6580,1,9999999,3,0));
        items.add(new Item("Episode 3",2,4780,1,1234567,3,0));

        long numItems = getItemCount();
        if(numItems == 0){
            for ( Item i: items){
                try{
                    createItem(i);
                } catch (SQLiteException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String getRoomName(int roomId) {
        String[] ids = {Integer.toString(roomId)};
        Cursor cursor = mDatabase.query(RoomsTable.TABLE_NAME,RoomsTable.ALL_COLUMNS,RoomsTable.COLUMN_ID + "=?",ids,null,null,null);
        String name = "Error";
        if(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(RoomsTable.COLUMN_NAME));
        }
        cursor.close();
        return name;
    }

    public String getStorageName(int storageId) {
        String[] ids = {Integer.toString(storageId)};
        Cursor cursor = mDatabase.query(StoragesTable.TABLE_NAME,StoragesTable.ALL_COLUMNS,StoragesTable.COLUMN_ID + "=?",ids,null,null,null);
        String name = "Error";
        if(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(RoomsTable.COLUMN_NAME));
        }
        cursor.close();
        return name;
    }

    //increment and decrement functions return the new count of the item.
    public int incrementItemCount(int primaryKey){
        String key = Integer.toString(primaryKey);
        String[] args = {"1",key};
        String[] ids = {key};
        mDatabase.execSQL("UPDATE " + ItemsTable.TABLE_NAME + " SET " + ItemsTable.COLUMN_COUNT + " = " + ItemsTable.COLUMN_COUNT + " + ? " + " WHERE " + ItemsTable.COLUMN_KEY + " = ?", args);
        Cursor cursor = mDatabase.query(ItemsTable.TABLE_NAME,ItemsTable.ALL_COLUMNS,ItemsTable.COLUMN_KEY + "=?",ids,null,null,ItemsTable.COLUMN_ID);
        int count = -1;
        while (cursor.moveToNext()) {
            count = cursor.getInt(cursor.getColumnIndex(ItemsTable.COLUMN_COUNT));
        }
        cursor.close();
        return count;
    }

    public int decrementItemCount(int primaryKey){
        String key = Integer.toString(primaryKey);
        String[] args = {"1",key};
        String[] ids = {key};
        mDatabase.execSQL("UPDATE " + ItemsTable.TABLE_NAME + " SET " + ItemsTable.COLUMN_COUNT + " = " + ItemsTable.COLUMN_COUNT + " - ? " + " WHERE " + ItemsTable.COLUMN_KEY + " = ?", args);
        Cursor cursor = mDatabase.query(ItemsTable.TABLE_NAME,ItemsTable.ALL_COLUMNS,ItemsTable.COLUMN_KEY + "=?",ids,null,null,ItemsTable.COLUMN_ID);
        int count = -1;
        while (cursor.moveToNext()) {
            count = cursor.getInt(cursor.getColumnIndex(ItemsTable.COLUMN_COUNT));
        }
        cursor.close();
        return count;
    }

    public void deleteItem(int primaryKey){
        String whereClause = ItemsTable.COLUMN_KEY + "=?";
        String[] args = {Integer.toString(primaryKey)};
        mDatabase.delete(ItemsTable.TABLE_NAME,whereClause,args);
    }

    public void deleteStorage(int primaryKey){
        String whereClause = StoragesTable.COLUMN_KEY + "=?";
        String[] args = {Integer.toString(primaryKey)};
        mDatabase.delete(StoragesTable.TABLE_NAME,whereClause,args);
    }

    public void deleteRoom(int primaryKey){
        String whereClause = RoomsTable.COLUMN_KEY + "=?";
        String[] args = {Integer.toString(primaryKey)};
        mDatabase.delete(RoomsTable.TABLE_NAME,whereClause,args);
    }
}
