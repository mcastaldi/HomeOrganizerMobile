package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.House;

//Used with the landing page and overall home view screen
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    public DataSource mDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //connect to database
        try {
            mDataSource = new DataSource(this);
            mDataSource.open();
            //used on the first time the app is run to add some demo information
            mDataSource.seedRooms();
            mDataSource.seedStorages();
            mDataSource.seedItems();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //create the navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setup the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //load the house and setup the list of rooms
        House theHouse = mDataSource.loadHouse();
        RoomAdapter adapter = new RoomAdapter(this,theHouse);

        //access the view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.roomRecyclerView);
        recyclerView.setAdapter(adapter);

        //Show the correct deleted message if something was deleted
        if(getIntent().getExtras() != null){
            String deleted = getIntent().getExtras().getString("deleted");
            if(deleted != null) {
                if (deleted.equalsIgnoreCase("item")) {
                    Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
                } else if (deleted.equalsIgnoreCase("storage")) {
                    Toast.makeText(this, "Storage Deleted", Toast.LENGTH_SHORT).show();
                } else if(deleted.equalsIgnoreCase("room")){
                    Toast.makeText(this, "Room Deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mDataSource.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mDataSource.open();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
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
            Toast.makeText(HomeActivity.this,"You're already there.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_add_item) {
            Intent intent = new Intent(HomeActivity.this,AddItemActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_room) {
            Intent intent = new Intent(HomeActivity.this,AddRoomActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_storage) {
            Intent intent = new Intent(HomeActivity.this,AddStorageActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_overall_info) {
            Intent intent = new Intent(HomeActivity.this,OverallInfoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_getting_started){
            Intent intent = new Intent(HomeActivity.this,GettingStartedActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
