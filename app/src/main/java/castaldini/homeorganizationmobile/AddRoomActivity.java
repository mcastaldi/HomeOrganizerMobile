package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.Room;

public class AddRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Room addedRoom;
    DataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addedRoom = null;
        mDataSource = new DataSource(this);
        mDataSource.open();

        drawer = (DrawerLayout) findViewById(R.id.room_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final EditText addRoomName = (EditText) findViewById(R.id.addRoomName);
        final EditText addRoomFloor = (EditText) findViewById(R.id.addRoomFloor);

        final Button addRoomButton = (Button) findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!(isEmpty(addRoomName)||isEmpty(addRoomFloor)))
                {
                    String roomName = addRoomName.getText().toString();
                    String roomFloor = addRoomFloor.getText().toString();
                    int roomId = (int) mDataSource.getRoomCount();
                    addedRoom = mDataSource.createRoom(new Room(roomFloor,roomName,roomId));

                    //Say the room was created and clear text fields
                    Toast.makeText(AddRoomActivity.this, "Added Room: " + addedRoom.getName(), Toast.LENGTH_SHORT).show();
                    addRoomName.setText("");
                    addRoomFloor.setText("");
                }
            }
        });
    }

    private boolean isEmpty(EditText etText){
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.room_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.house_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, PrefsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_license){
            Snackbar.make(drawer,R.string.license_text, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AddRoomActivity.this,HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_item) {
            Intent intent = new Intent(AddRoomActivity.this,AddItemActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_room) {
            //Toast.makeText(AddRoomActivity.this,"You're already there.",Toast.LENGTH_SHORT);

        } else if (id == R.id.nav_add_storage) {
            Intent intent = new Intent(AddRoomActivity.this,AddStorageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_overall_info) {
            Intent intent = new Intent(AddRoomActivity.this,OverallInfoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_getting_started){
            Intent intent = new Intent(AddRoomActivity.this,GettingStartedActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
