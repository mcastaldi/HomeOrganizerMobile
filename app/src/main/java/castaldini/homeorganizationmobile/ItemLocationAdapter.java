package castaldini.homeorganizationmobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import castaldini.homeorganizationmobile.model.ItemLocation;

/**
 *  Class used to display the suggested location items in the list
 */

class ItemLocationAdapter extends RecyclerView.Adapter<ItemLocationAdapter.ViewHolder> {

    private Context mContext;
    private List<ItemLocation> mLocations;
    private ItemLocationAdapterListener mListener;

    public ItemLocationAdapter(Context context,List<ItemLocation> itemLocations){
        this.mContext = context;
        this.mLocations = itemLocations;
        mListener = (ItemLocationAdapterListener) context;
    }

    @Override
    public ItemLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View locationView = inflater.inflate(R.layout.list_location,parent,false);

        return new ViewHolder (locationView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ItemLocation itemLocation = mLocations.get(position);

        holder.tvRoomName.setText(itemLocation.getRoomName());
        holder.tvStorageName.setText(itemLocation.getStorageId());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemLocationSelected(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

//    public void updateView(List<ItemLocation> itemLocations){
//        this.mLocations = itemLocations;
//
//    }
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvRoomName;
        TextView tvStorageName;
        View mView;

        ViewHolder(View locationView){
            super(locationView);

            tvRoomName = (TextView) locationView.findViewById(R.id.location_room);
            tvStorageName = (TextView) locationView.findViewById(R.id.location_storage);
            mView = locationView;
        }
    }

    interface ItemLocationAdapterListener{
        void onItemLocationSelected(int pos);
    }
}
