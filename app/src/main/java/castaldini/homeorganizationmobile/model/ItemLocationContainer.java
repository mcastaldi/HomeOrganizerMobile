package castaldini.homeorganizationmobile.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 *  Class used to store item locations so they can be sent in intents
 */

public class ItemLocationContainer implements Parcelable {
    private List<ItemLocation> locations;

    public ItemLocationContainer(List<ItemLocation> locations){
        this.locations = locations;
    }

    public List<ItemLocation> getLocations(){
        return this.locations;
    }

    private ItemLocationContainer(Parcel in) {
        this.locations = new ArrayList<>();
        in.readList(this.locations,ItemLocation.class.getClassLoader());
    }

    //The remaining functions allow this object to be sent as an extra between activities
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.locations);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemLocationContainer> CREATOR = new Creator<ItemLocationContainer>() {
        @Override
        public ItemLocationContainer createFromParcel(Parcel in) {
            return new ItemLocationContainer(in);
        }

        @Override
        public ItemLocationContainer[] newArray(int size) {
            return new ItemLocationContainer[size];
        }
    };
}
