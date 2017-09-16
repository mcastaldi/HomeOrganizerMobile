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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.Storage;

/**
 * Class used to control the screen that the users uses to add storages to their inventory.
 */
public class AddStorageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    Storage addedStorage;
    DataSource mDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_storage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.storage_drawer_layout);

        addedStorage = null;
        mDataSource = new DataSource(this);
        mDataSource.open();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Fill in the select room spinner
        final Spinner selectRoom = (Spinner) findViewById(R.id.addStorageSelectRoom);
        ArrayAdapter<CharSequence> selectRoomAdapter =
                ArrayAdapter.createFromResource(this,R.array.tempRooms,android.R.layout.simple_spinner_item);
        selectRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectRoom.setAdapter(selectRoomAdapter);


        final EditText addStorageName = (EditText) findViewById(R.id.addStorageName);
        final EditText addStorageCost = (EditText) findViewById(R.id.addStorageCost);

        final Button addStorageButton = (Button) findViewById(R.id.addStorageButton);
        addStorageButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(!(isEmpty(addStorageName)||isEmpty(addStorageCost)))
                {
                    String storageName = addStorageName.getText().toString();
                    int storageCost = ((int) Double.parseDouble(addStorageCost.getText().toString()));
                    int roomId = selectRoom.getSelectedItemPosition();
                    int storageId = mDataSource.getNumStoragesInRoom(roomId);
                    addedStorage = mDataSource.createStorage(new Storage(storageName, storageId,storageCost,roomId));
                    mDataSource.createStorage(addedStorage);

                    //Say the storage was added then clear text fields
                    Toast.makeText(AddStorageActivity.this, "Added Storage: " + addedStorage.getName(), Toast.LENGTH_SHORT).show();
                    addStorageName.setText("");
                    addStorageCost.setText("");
                }
            }
        });
    }

    private boolean isEmpty(EditText etText){
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.storage_drawer_layout);
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
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(AddStorageActivity.this,HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_item) {
            Intent intent = new Intent(AddStorageActivity.this,AddItemActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_room) {
            Intent intent = new Intent(AddStorageActivity.this,AddRoomActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_storage) {
            Toast.makeText(AddStorageActivity.this,"You're already there.",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_overall_info) {
            Intent intent = new Intent(AddStorageActivity.this,OverallInfoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_getting_started){
            Intent intent = new Intent(AddStorageActivity.this,GettingStartedActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
