package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.Room;

/**
 * Class that controls the screen that shows the information on a room. Uses the storage adapter to show a list of the storages in the room
 */
public class RoomDetailActivity extends AppCompatActivity {

    public DataSource mDataSource;
    Room mRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataSource = new DataSource(this);
        mDataSource.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        if(getIntent().getExtras()!=null) {
            mRoom = getIntent().getExtras().getParcelable(RoomAdapter.ROOM_KEY);
            if (mRoom != null) {

                TextView tvRoomName = (TextView) findViewById(R.id.viewStoragesRoomName);
                TextView tvRoomFloor = (TextView) findViewById(R.id.viewStoragesRoomFloor);
                tvRoomName.setText(mRoom.getName());
                Resources res = getResources();
                tvRoomFloor.setText(res.getString(R.string.room_floor,mRoom.getFloor()));

                StorageAdapter adapter = new StorageAdapter(this, mRoom);
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.storagesRecyclerView);
                recyclerView.setAdapter(adapter);

            } else {
                Toast.makeText(this, "Error loading room", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void deleteRoomButtonClicked(View view) {
        if(mRoom.getNumStorages()>0){
            Toast.makeText(this, "Can't delete when there are storages", Toast.LENGTH_SHORT).show();
        } else {
            mDataSource.deleteRoom(mRoom.getPrimaryKey());
            Intent intent = new Intent(RoomDetailActivity.this,HomeActivity.class);
            intent.putExtra("deleted","room");
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDataSource.close();
    }
}
