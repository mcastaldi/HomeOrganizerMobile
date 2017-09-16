package castaldini.homeorganizationmobile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import castaldini.homeorganizationmobile.model.House;
import castaldini.homeorganizationmobile.model.Room;

/**
 * Class that fills the list of rooms in the home activity
 */

class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private static final String HOUSE_KEY = "house_key";
    static final String ROOM_KEY = "room_key";
    private List<Room> mRooms;
    private House mHouse;
    private Context mContext;

    RoomAdapter(Context context, House house) {
        this.mHouse = house;
        this.mContext = context;
        this.mRooms = house.getRooms();
    }

    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.list_room, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RoomAdapter.ViewHolder holder, int position) {
        final Room room = mRooms.get(position);

        Resources res = mContext.getResources();
        holder.tvRoomName.setText(room.getName());
        holder.tvRoomFloor.setText(res.getString(R.string.room_floor,room.getFloor()));
        holder.tvRoomNumStorages.setText(res.getString(R.string.room_storages,room.getNumStorages()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RoomDetailActivity.class);
                intent.putExtra(ROOM_KEY, room);
                intent.putExtra(HOUSE_KEY,mHouse);
                mContext.startActivity(intent);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "You long clicked " + room.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRooms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRoomName;
        TextView tvRoomFloor;
        TextView tvRoomNumStorages;
        View mView;
        ViewHolder(View itemView) {
            super(itemView);

            tvRoomName = (TextView) itemView.findViewById(R.id.viewRoomListName);
            tvRoomFloor = (TextView) itemView.findViewById(R.id.viewRoomListFloor);
            tvRoomNumStorages = (TextView) itemView.findViewById(R.id.viewRoomListStorageCount);
            mView = itemView;
        }
    }
}