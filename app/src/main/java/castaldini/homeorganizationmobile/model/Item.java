package castaldini.homeorganizationmobile.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.database.ItemsTable;

/**
 *  Class used to represent the individual items stored in the house
 */

public class Item implements Parcelable {

    private int id, barcode, count, roomId, storageId;
    private int primaryKey;
    private int cost;
    private String name;

    /**
     * Null constructor for the Item class
     */
    public Item(){
    }

    /**
     * Copy constructor for the Item class
     *
     * @param i an Item
     */
    public Item(Item i){
        this.name = i.getName();
        this.id = i.getId();
        this.cost = i.getCost();
        this.count = i.getCount();
        this.barcode = i.getBarcode();
        this.roomId = i.getRoomId();
        this.storageId = i.getStorageId();
    }

    /**
     * Constructor used to create an actual new item
     *
     * @param name name of new item
     * @param id id of new item
     * @param cost cost of new item
     * @param count number of the new item
     * @param barcode barcode of the new item
     * @param roomId id of the room the item is in
     * @param storageId id of the storage the item is in
     */
    public Item(String name, int id, int cost, int count, int barcode, int roomId, int storageId){
        this.name = name;
        this.id = id;
        //this.consumable = consumable;
        this.cost = cost;
        this.count = count;
        this.barcode = barcode;
        this.roomId = roomId;
        this.storageId=storageId;
    }

    /**
     * @return count Int
     */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int increment(DataSource mDatasource){
        this.count = mDatasource.incrementItemCount(this.primaryKey);
        return count;
    }
    public int decrement(DataSource mDatasource){
        this.count = mDatasource.decrementItemCount(this.primaryKey);
        return count;
    }
    /**
     * @return id Int
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrimaryKey(int primaryKey){this.primaryKey = primaryKey;}
    /**
     * @return barcode Int
     */
    public int getBarcode() {
        return barcode;
    }

    public void setBarcode(int barcode) {
        this.barcode = barcode;
    }

    /**
     * @return cost Int
     */
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCostAsString(){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(((double) cost) / 100.0);
    }
    /**
     * @return name String
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return id of the room the item is in
     */
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    /**
     * @return id of the storage the item is in
     */
    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public int getPrimaryKey(){return primaryKey;}
    public ContentValues toValues(){
        ContentValues values = new ContentValues(7);

        values.put(ItemsTable.COLUMN_ID,id);
        values.put(ItemsTable.COLUMN_NAME,name);
        values.put(ItemsTable.COLUMN_COST,cost);
        values.put(ItemsTable.COLUMN_COUNT,count);
        values.put(ItemsTable.COLUMN_BARCODE,barcode);
        values.put(ItemsTable.COLUMN_ROOM,roomId);
        values.put(ItemsTable.COLUMN_STORAGE,storageId);
        return values;
    }

    public String toString(){
        return barcode + ": " + name + ", $" + cost + ", " + count +", " + roomId + ": " + storageId;
    }

    //The remaining functions allow this object to be sent as an extra between activities
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeInt(this.id);
        dest.writeInt(this.barcode);
        dest.writeInt(this.count);
        dest.writeInt(this.roomId);
        dest.writeInt(this.storageId);
        dest.writeInt(this.cost);
        dest.writeString(this.name);
    }

    protected Item(Parcel in) {
        this.primaryKey = in.readInt();
        this.id = in.readInt();
        this.barcode = in.readInt();
        this.count = in.readInt();
        this.roomId = in.readInt();
        this.storageId = in.readInt();
        this.cost = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
