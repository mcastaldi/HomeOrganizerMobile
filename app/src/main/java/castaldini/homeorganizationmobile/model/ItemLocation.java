package castaldini.homeorganizationmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  Class used to store the suggested location to store an item
 */

public class ItemLocation implements Parcelable{
    private String roomName, storageName;
    private int roomId, storageId;

    public ItemLocation(Storage storage){
        this.roomName = storage.getRoomName();
        this.roomId = storage.getRoomId();
        this.storageName = storage.getName();
        this.storageId = storage.getId();
    }

    private ItemLocation(Parcel in) {
        roomName = in.readString();
        storageName = in.readString();
        roomId = in.readInt();
        storageId = in.readInt();
    }

    public static final Creator<ItemLocation> CREATOR = new Creator<ItemLocation>() {
        @Override
        public ItemLocation createFromParcel(Parcel in) {
            return new ItemLocation(in);
        }

        @Override
        public ItemLocation[] newArray(int size) {
            return new ItemLocation[size];
        }
    };

    /**
     * @return suggested room name
     */
    public String getRoomName() {
        return roomName;
    }

    /**
     * @return suggested room id
     */
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    /**
     * @return suggested storage id
     */
    public int getStorageId() {
        return storageId;
    }

    @Override
    public String toString(){
        return roomName + ": " + storageName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeString(storageName);
        dest.writeInt(roomId);
        dest.writeInt(storageId);
    }
}
