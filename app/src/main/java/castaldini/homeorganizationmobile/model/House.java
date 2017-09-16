package castaldini.homeorganizationmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *  Container class used to hold the rooms and get meta information about everything stored by the user
 */

public class House implements Parcelable{
    private List<Room> rooms;

    public House(List<Room> rooms){
        this.rooms = rooms;
    }



    /**
     * @param barcode Barcode of item to be found
     * @return Object of found item, or empty item.
     */
    public Item findItem(int barcode){
        Item foundItem = new Item();
        boolean found = false;
        for( Room r: rooms){
            for( Storage s: r.getStorages()){
                for( Item i: s.getItems()){
                    if(barcode == i.getBarcode()){
                        found = true;
                        foundItem = i;
                        break;
                    }
                }
                if(found)
                    break;
            }
            if(found)
                break;
        }
        if(!found)
            return null;
        else
            return foundItem;

    }

    public int getRoomCount(){
        return rooms.size();
    }

    public int getStorageCount(){
        int storageCount = 0;

        for(Room r: rooms){
            storageCount += r.getNumStorages();
        }

        return storageCount;
    }

    public int getItemCount(){
        int itemCount = 0;

        for(Room r: rooms){
            for(Storage s: r.getStorages()){
                itemCount+= s.getNumOfItems();
            }
        }

        return itemCount;
    }

    public int getTotalCost(){
        int totalCost = 0;

        for(Room r: rooms){
            for(Storage s: r.getStorages()){
                totalCost += s.getCost();
                for(Item i: s.getItems()){
                    totalCost += i.getCost();
                }
            }
        }

        return totalCost;
    }

    public String getTotalCostAsString(){
        int totalCost = 0;

        for(Room r: rooms){
            for(Storage s: r.getStorages()){
                totalCost += s.getCost();
                for(Item i: s.getItems()){
                    totalCost += i.getCost();
                }
            }
        }

        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(((double) totalCost) / 100.0);
    }


    public List<Room> getRooms(){
        return this.rooms;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.rooms);
    }

    private House(Parcel in) {
        this.rooms = new ArrayList<>();
        in.readList(this.rooms,Room.class.getClassLoader());
    }

    public static final Creator<House> CREATOR = new Creator<House>() {
        @Override
        public House createFromParcel(Parcel in) {
            return new House(in);
        }

        @Override
        public House[] newArray(int size) {
            return new House[size];
        }
    };
}
