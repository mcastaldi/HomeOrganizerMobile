package castaldini.homeorganizationmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import castaldini.homeorganizationmobile.model.Room;
import castaldini.homeorganizationmobile.model.Storage;

/**
 *  Used to fill in the area in the room detail activity that lists all of the storages in that room.
 *
 */

class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.ViewHolder> {

    static final String STORAGE_KEY = "storage_key";
    private static final int STORAGE_REQUEST_CODE = 1;
    private List<Storage> mStorages;
    private Room mRoom;
    private Context mContext;

    //fill in the information whenever the adapter is created
    StorageAdapter(Context context, Room room) {
        this.mContext = context;
        this.mStorages = room.getStorages();
        this.mRoom = room;
    }

    @Override
    public StorageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.list_storage, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(StorageAdapter.ViewHolder holder, int position) {
        final Storage storage = mStorages.get(position);

        holder.tvStorageName.setText(storage.getName());
        holder.tvStorageCost.setText(storage.getCostAsString());
        Resources res = mContext.getResources();
        holder.tvStorageNumItems.setText(res.getString(R.string.storage_num_items,storage.getNumOfItems()));

        //listener to go to the selected storage
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StorageDetailActivity.class);
                intent.putExtra(STORAGE_KEY,storage);
                intent.putExtra(RoomAdapter.ROOM_KEY,mRoom);
                ((Activity)mContext).startActivityForResult(intent,STORAGE_REQUEST_CODE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStorages.size();
    }

    //extended sub-class that sets up the items in the view
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStorageName;
        TextView tvStorageCost;
        TextView tvStorageNumItems;
        View mView;
        ViewHolder(View itemView) {
            super(itemView);

            tvStorageName = (TextView) itemView.findViewById(R.id.viewStorageListName);
            tvStorageCost = (TextView) itemView.findViewById(R.id.viewStorageListCost);
            tvStorageNumItems = (TextView) itemView.findViewById(R.id.viewStorageListItemCount);
            mView = itemView;
        }
    }
}
