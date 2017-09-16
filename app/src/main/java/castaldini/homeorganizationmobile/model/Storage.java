package castaldini.homeorganizationmobile.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.database.StoragesTable;

/**
 * Storage class used to represent storages in the application
 */

public class Storage implements Parcelable {
    private String name, roomName;
    private int id,roomId,cost,primaryKey;


    private List<Item> items;
    private final List<Integer> itemIds;

    public Storage(String name, int id, int cost, int roomId, List<Item> items){
        this.name = name;
        this.id = id;
        this.cost = cost;
        this.roomId = roomId;
        this.items = items;
        this.itemIds = new ArrayList<>();
    }
    /**
     * Constructor for creating a storage that was loaded from the save file.
     * @param name Name of storage created.
     * @param id Id of storage created.
     * @param cost Cost of storage created, in cents.
     * @param roomId Id of the room the storage is in
     */
    public Storage(String name, int id, int cost, int roomId){
        this.cost = cost;
        this.name = name;
        this.id = id;
        this.items = new ArrayList<>();
        this.itemIds = new ArrayList<>();
        this.roomId = roomId;
    }
    /**
     * Constructor for creating an empty storage.
     */
    public Storage(){
        this.items = new ArrayList<>();
        this.itemIds = new ArrayList<>();
    }
    /**
     * @return Cost of storage, in cents.
     */
    public int getCost() {return cost;}


    public String getCostAsString(){
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(((double) cost) / 100.0);
    }

    /**
     * @param cost Set the cost of the storage in cents.
     */
    public void setCost(int cost) {this.cost = cost;}
    /**
     * @return Name of this storage.
     */
    public String getName() {return name;}
    /**
     * @param name New name of this storage.
     */
    public void setName(String name) {this.name = name;}
    /**
     * @return Id of this storage.
     */
    public int getId() {return id;}
    /**
     * @param id New id of this storage.
     */
    public void setId(int id) {this.id = id;}
    /**
     * @return Number of items in this storage.
     */
    public int getNumOfItems() {return items.size();}
    /**
     * @return List of all items in this storage.
     */
    public List<Item> getItems() {return items;}
    /**
     * @return Id of the room this storage is in.
     */
    public int getRoomId() {return roomId;}
    /**
     * @param roomId New id of the room this storage is in.
     */
    public void setRoomId(int roomId) {this.roomId = roomId;}

    String getRoomName() { return roomName;}

    public void setRoomName(String roomName){ this.roomName = roomName;}
    public void setItems(List<Item> items){
        this.items = items;
    }

    public int getPrimaryKey(){return this.primaryKey;}
    public void setPrimaryKey(int primaryKey){this.primaryKey = primaryKey;}
    @Override
    public String toString(){
        return roomId +": " + id + ": " + name + ", " + cost;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(4);

        values.put(StoragesTable.COLUMN_ID,id);
        values.put(StoragesTable.COLUMN_NAME,name);
        values.put(StoragesTable.COLUMN_COST,cost);
        values.put(StoragesTable.COLUMN_ROOM,roomId);

        return values;
    }

    //The remaining functions allow this object to be sent as an extra between activities
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.primaryKey);
        dest.writeString(this.name);
        dest.writeString(this.roomName);
        dest.writeInt(this.id);
        dest.writeInt(this.roomId);
        dest.writeInt(this.cost);
        dest.writeTypedList(this.items);
        dest.writeList(this.itemIds);
    }

    protected Storage(Parcel in) {
        this.primaryKey = in.readInt();
        this.name = in.readString();
        this.roomName = in.readString();
        this.id = in.readInt();
        this.roomId = in.readInt();
        this.cost = in.readInt();
        this.items = in.createTypedArrayList(Item.CREATOR);
        this.itemIds = new ArrayList<>();
        in.readList(this.itemIds, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Storage> CREATOR = new Parcelable.Creator<Storage>() {
        @Override
        public Storage createFromParcel(Parcel source) {
            return new Storage(source);
        }

        @Override
        public Storage[] newArray(int size) {
            return new Storage[size];
        }
    };
}
