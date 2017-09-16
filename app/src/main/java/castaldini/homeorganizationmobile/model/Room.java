package castaldini.homeorganizationmobile.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.database.RoomsTable;

/**
 * Room Class used to represent the rooms in the app.
 */

public class Room implements Parcelable {
    private String floor,name;
    private int id;
    private List<Storage> storages;
    private final List<Integer> storageIds;
    private int primaryKey;

    public Room(String floor, String name, int id, List<Storage> storages){
        this.name = name;
        this.floor = floor;
        this.id = id;
        this.storages = storages;
        this.storageIds = new ArrayList<>();
    }
    /**
     * Create a new room object that was loaded from the save file.
     * @param floor Type of room.
     * @param name Name of room.
     * @param id  Id of room.
     */
    public Room(String floor, String name, int id){
        this.name = name;
        this.floor = floor;
        this.id=id;
        this.storages = new ArrayList<>();
        this.storageIds = new ArrayList<>();
    }
    /**
     * Create an empty room object.
     */
    public Room(){
        this.storages = new ArrayList<>();
        this.storageIds = new ArrayList<>();
    }

    /**
     * @return This rooms floor
     */
    public String getFloor() {return floor;}
    /**
     * @param floor New floor to set this room to.
     */
    public void setFloor(String floor) {this.floor = floor;}
    /**
     * @return Name of this room.
     */
    public String getName() {return name;}
    /**
     * @param name New name to give this room.
     */
    public void setName(String name) {this.name = name;}
    /**
     * @return Id of this room.
     */
    public int getId() {return id;}
    /**
     * @param id New id for this room.
     */
    public void setId(int id) {this.id = id;}

    /**
     * @return List of all storages in this room.
     */
    public List<Storage> getStorages(){return storages;}

    /**
     *
     * @return Number of storages in the room.
     */
    public int getNumStorages(){return storages.size();}
    /**
     * @param storages List of storages in the room.
     */
    public void setStorages(List<Storage> storages){this.storages = storages;}

    public int getPrimaryKey(){return this.primaryKey;}
    public void setPrimaryKey(int primaryKey){this.primaryKey = primaryKey;}
    @Override
    public String toString(){
        return id + ": " + name + ", " + floor;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(7);

        values.put(RoomsTable.COLUMN_ID,id);
        values.put(RoomsTable.COLUMN_NAME,name);
        values.put(RoomsTable.COLUMN_FLOOR,floor);
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
        dest.writeString(this.floor);
        dest.writeString(this.name);
        dest.writeInt(this.id);
        dest.writeList(this.storages);
        dest.writeList(this.storageIds);
    }

    protected Room(Parcel in) {
        this.primaryKey = in.readInt();
        this.floor = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
        this.storages = new ArrayList<>();
        in.readList(this.storages, Storage.class.getClassLoader());
        this.storageIds = new ArrayList<>();
        in.readList(this.storageIds, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
