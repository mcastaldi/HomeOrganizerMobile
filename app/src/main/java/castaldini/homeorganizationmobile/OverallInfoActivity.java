package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.House;

/**
 *  Class that controls the screen that shows meta information to the user. No interaction outside of the menus.
 */
public class OverallInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    DataSource mDataSource;
    House theHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_overall_info);try {
            mDataSource = new DataSource(this);
            mDataSource.open();
        } catch (SQLException e){
            e.printStackTrace();
        }

        theHouse = mDataSource.loadHouse();
        TextView roomCount = (TextView) findViewById(R.id.roomCount);
        TextView storageCount = (TextView) findViewById(R.id.storageCount);
        TextView itemCount = (TextView) findViewById(R.id.itemCount);
        TextView totalCost = (TextView) findViewById(R.id.totalCost);

        roomCount.setText(String.valueOf(theHouse.getRoomCount()));
        storageCount.setText(String.valueOf(theHouse.getStorageCount()));
        itemCount.setText(String.valueOf(theHouse.getItemCount()));
        totalCost.setText(theHouse.getTotalCostAsString());

        drawer = (DrawerLayout) findViewById(R.id.overall_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.overall_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.overall_drawer_layout);
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
            Intent intent = new Intent(OverallInfoActivity.this,HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_item) {
            Intent intent = new Intent(OverallInfoActivity.this,AddItemActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_room) {
            Intent intent = new Intent(OverallInfoActivity.this,AddRoomActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_storage) {
            Intent intent = new Intent(OverallInfoActivity.this,AddStorageActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_overall_info) {
            Toast.makeText(OverallInfoActivity.this,"You're already there.",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_getting_started){
            Intent intent = new Intent(OverallInfoActivity.this,GettingStartedActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
