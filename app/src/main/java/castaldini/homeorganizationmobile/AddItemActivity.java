package castaldini.homeorganizationmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.House;
import castaldini.homeorganizationmobile.model.Item;
import castaldini.homeorganizationmobile.model.ItemLocation;
import castaldini.homeorganizationmobile.model.ItemLocationContainer;
import castaldini.homeorganizationmobile.model.Room;
import castaldini.homeorganizationmobile.model.Storage;
import castaldini.homeorganizationmobile.services.SuggestionService;

/*
    AddItemActivity Class controls what happens in the add item screen.
    The classes that it implements deal with either the navigation menu or one of the many dialogs that can be shown.
 */
public class AddItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SuggestDialogFragment.SuggestDialogListener,
        SuggestedResultsDialogFragment.SuggestedResultsListener, ScannerPromptDialogFragment.ScannerPromptListener,  ItemLocationAdapter.ItemLocationAdapterListener {

    //variables used
    public static final String TAG = "AUC_SUGGEST";
    private static final int RC_BARCODE_CAPTURE = 9001;
    DrawerLayout drawer;
    Item addedItem;
    DataSource mDataSource;
    Spinner selectRoom;
    Spinner selectStorage;
    List<Storage> storages;
    List<Room> rooms;
    TextView selectRoomLabel;
    TextView selectStorageLabel;
    EditText addItemBarcode;
    TextView addItemNameLabel;
    EditText addItemName;
    TextView addItemCountLabel;
    EditText addItemCount;
    TextView addItemCostLabel;
    EditText addItemCost;
    Button addItemButton;
    List<ItemLocation> suggestedLocations;
    ItemLocation selectedLocation;
    SharedPreferences settings;
    SuggestedResultsDialogFragment resultsDialog;

    //Used to get the response from the service getting suggested locations from the server
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ItemLocationContainer container = intent.getParcelableExtra("suggestedLocations");
            List<ItemLocation> suggestLocations = container.getLocations();
            resultsDialog = SuggestedResultsDialogFragment.newInstance(suggestLocations);
            suggestedLocations =suggestLocations;
            resultsDialog.show(getSupportFragmentManager(),"SuggestedResultsDialogFragment");
        }
    };

    //Called whenever the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set up the receiver for the suggested locations.
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, new IntentFilter(SuggestionService.SUGGESTION_SERVICE_MESSAGE));
        setContentView(R.layout.activity_add_item);

        //Set up the top and side navigation areas
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.item_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //connect to database
        try {
            mDataSource = new DataSource(this);
            mDataSource.open();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //Setup all of the variables by selecting them from the layout file
        selectRoomLabel = (TextView) findViewById(R.id.addItemSelectRoomLabel);
        selectRoom = (Spinner) findViewById(R.id.addItemSelectRoom);
        selectStorageLabel = (TextView) findViewById(R.id.addItemSelectStorageLabel);
        selectStorage = (Spinner) findViewById(R.id.addItemSelectStorage);
        addItemBarcode = (EditText) findViewById(R.id.addItemBarcode);
        addItemNameLabel = (TextView) findViewById(R.id.addItemNameLabel);
        addItemName = (EditText) findViewById(R.id.addItemName);
        addItemCountLabel = (TextView) findViewById(R.id.addItemCountLabel);
        addItemCount = (EditText) findViewById(R.id.addItemCount);
        addItemCostLabel = (TextView) findViewById(R.id.addItemCostLabel);
        addItemCost = (EditText) findViewById(R.id.addItemCost);
        addItemButton = (Button) findViewById(R.id.addItemButton);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        rooms = mDataSource.loadRooms();
        final Button submitBarcodeButton = (Button) findViewById(R.id.submitBarcodeButton);
        hideAddItemForm();

        addedItem = null;

        //Listener for deciding what to set the button as, either "Scan" or "Submit"
        addItemBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(isEmpty(addItemBarcode)&&(settings.getBoolean(getString(R.string.ask_scanner_pref), false)||settings.getBoolean(getString(R.string.scanner_pref), false))){
                    submitBarcodeButton.setText(R.string.scan);
                } else {
                    submitBarcodeButton.setText(R.string.submit);
                }
            }
        });

        //listener used to update the storage spinner whenever the room spinner is changed.
        selectRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id){
                storages = mDataSource.getStoragesInRoom(position);
                List<String> names = new ArrayList<>();
                for(Storage s: storages){
                    names.add(s.getName());
                }
                String[] storageNames = names.toArray(new String[0]);
                Toast.makeText(parent.getContext(),parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();
                ArrayAdapter<String> selectStorageAdapter = new ArrayAdapter<>(parent.getContext(),android.R.layout.simple_spinner_dropdown_item,storageNames);
                selectStorageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectStorage.setAdapter(selectStorageAdapter);
                if(selectedLocation != null)
                    selectStorage.setSelection(selectedLocation.getStorageId());
            }

            public void onNothingSelected(AdapterView<?> parent){}

        });

    }

    //cleanup whenever the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataSource.close();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
    }

    //Function to check if a text view is empty
    private boolean isEmpty(EditText etText){
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.item_drawer_layout);
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
            Intent intent = new Intent(AddItemActivity.this,HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_item) {
            hideAddItemForm();

        } else if (id == R.id.nav_add_room) {
            Intent intent = new Intent(AddItemActivity.this,AddRoomActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_add_storage) {
            Intent intent = new Intent(AddItemActivity.this,AddStorageActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_overall_info) {
            Intent intent = new Intent(AddItemActivity.this,OverallInfoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_getting_started){
            Intent intent = new Intent(AddItemActivity.this,GettingStartedActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showSuggestionDialog(){
        SuggestDialogFragment dialog = new SuggestDialogFragment();
        dialog.show(getSupportFragmentManager(),"SuggestDialogFragment");
    }

    @Override
    public void onConfirmGetSuggestion(android.support.v4.app.DialogFragment dialog) {
        getSuggestedLocations(Integer.parseInt(addItemBarcode.getText().toString()));
    }

    @Override
    public void onDenyGetSuggestion(android.support.v4.app.DialogFragment dialog) {
        showAddItemForm();
    }

    //clear the form and hide it
    public void hideAddItemForm(){

        addItemBarcode.setText("");
        addItemName.setText("");
        addItemCount.setText("");
        addItemCost.setText("");
        selectRoomLabel.setVisibility(View.GONE);
        selectRoom.setVisibility(View.GONE);
        selectStorageLabel.setVisibility(View.GONE);
        selectStorage.setVisibility(View.GONE);
        addItemNameLabel.setVisibility(View.GONE);
        addItemName.setVisibility(View.GONE);
        addItemCountLabel.setVisibility(View.GONE);
        addItemCount.setVisibility(View.GONE);
        addItemCostLabel.setVisibility(View.GONE);
        addItemCost.setVisibility(View.GONE);
        addItemButton.setVisibility(View.GONE);
    }

    //Show the form and fill in the rooms.
    public void showAddItemForm(){
        List<String> names = new ArrayList<>();
        for(Room r: rooms){
            names.add(r.getName());
        }
        String[] roomNames = names.toArray(new String[0]);
        ArrayAdapter<String> selectRoomAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,roomNames);
        selectRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectRoom.setAdapter(selectRoomAdapter);
        selectRoomLabel.setVisibility(View.VISIBLE);
        selectRoom.setVisibility(View.VISIBLE);
        selectStorageLabel.setVisibility(View.VISIBLE);
        selectStorage.setVisibility(View.VISIBLE);
        addItemNameLabel.setVisibility(View.VISIBLE);
        addItemName.setVisibility(View.VISIBLE);
        addItemCountLabel.setVisibility(View.VISIBLE);
        addItemCount.setVisibility(View.VISIBLE);
        addItemCostLabel.setVisibility(View.VISIBLE);
        addItemCost.setVisibility(View.VISIBLE);
        addItemButton.setVisibility(View.VISIBLE);
    }

    public void getSuggestedLocations(int barcode){
        //Start the service that will get the suggested locations for storing an item
        Intent intent = new Intent(this, SuggestionService.class);
        intent.setData(Uri.parse(Integer.toString(barcode)));
        House theHouse = new House(rooms);
        intent.putExtra("barcode",barcode);
        intent.putExtra("house",theHouse);
        startService(intent);
    }

    //triggers whenever the user selects a suggested location
    @Override
    public void onSelectSuggestedLocation(int pos) {
        //get what location was chosen
        selectedLocation = suggestedLocations.get(pos);
        showAddItemForm();
        selectRoom.setSelection(selectedLocation.getRoomId(),false);
    }

    @Override
    public void onCancelSuggestedLocation(){
        showAddItemForm();
        resultsDialog = null;
    }

    //Triggers whenever the submit button next to the barcode text view is clicked.
    public void submitBarcodeClicked(View view) {
        if(!isEmpty(addItemBarcode)) {//If it's not empty, then act like a submit button
            if (settings.getBoolean(getString(R.string.connect_pref), false)){//only give suggestions if the user wants to connect to the server
                if(settings.getBoolean(getString(R.string.ask_suggestion_pref),false) && !settings.getBoolean(getString(R.string.suggestion_pref),false)) {
                    showSuggestionDialog();
                }else if (settings.getBoolean(getString(R.string.suggestion_pref),false)){//auto assume yes
                    getSuggestedLocations(Integer.parseInt(addItemBarcode.getText().toString()));
                } else {
                    showAddItemForm();
                }
            }
            else
                showAddItemForm();
        } else {//if the text view is empty, ask if there should be a scanner
            if(settings.getBoolean(getString(R.string.ask_scanner_pref), false)||settings.getBoolean(getString(R.string.scanner_pref), false))
                showScannerPrompt();
        }
    }

    //Asks whether or not the user would like to use the barcode scanner.
    private void showScannerPrompt() {
        if(settings.getBoolean(getString(R.string.ask_scanner_pref), false)&&!settings.getBoolean(getString(R.string.scanner_pref), false)) {
            //ask
            ScannerPromptDialogFragment dialog = new ScannerPromptDialogFragment();
            dialog.show(getSupportFragmentManager(), "ScannerPromptDialogFragment");
        } else if (settings.getBoolean(getString(R.string.scanner_pref), false)){
            //assume yes
            this.onConfirmScanner(null);
        }
    }

    public void addItemButtonPressed(View view) {
        if(!(isEmpty(addItemBarcode)||isEmpty(addItemName)||isEmpty(addItemCount)||isEmpty(addItemCost)))
        {
            String itemName = addItemName.getText().toString();
            int itemBarcode = Integer.parseInt(addItemBarcode.getText().toString());
            int itemCount = Integer.parseInt(addItemCount.getText().toString());
            int itemCost = (int) (Double.parseDouble(addItemCost.getText().toString()) * 100);
            int roomId = selectRoom.getSelectedItemPosition();
            int storageId = selectStorage.getSelectedItemPosition();
            int itemId = mDataSource.getNumItemsInStorage(roomId,storageId);
            try {
                addedItem = mDataSource.createItem(new Item(itemName, itemId, itemCost, itemCount, itemBarcode, roomId, storageId));
            } catch (SQLException e){
                e.printStackTrace();
            }
            Toast.makeText(AddItemActivity.this, "Added Item: " + addedItem.getName(), Toast.LENGTH_SHORT).show();
            hideAddItemForm();
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    //got a barcode number and put it in the addItemBarcode text view
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(this, R.string.barcode_success, Toast.LENGTH_SHORT).show();
                    addItemBarcode.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //didn't capture a barcode, but didn't break
                    Toast.makeText(this,R.string.barcode_failure, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                //show error
                Toast.makeText(this, String.format(getString(R.string.barcode_error),CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConfirmScanner(android.support.v4.app.DialogFragment dialog) {
        //Start the barcode scanner activity
        Intent intent = new Intent(AddItemActivity.this,BarcodeCaptureActivity.class);
        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }

    @Override
    public void onCancelScanner(android.support.v4.app.DialogFragment dialog) {
    }

    @Override
    public void onItemLocationSelected(int pos) {

        //once an answer has been given, show the rest of the form and select the location.
        selectedLocation = suggestedLocations.get(pos);
        showAddItemForm();
        selectRoom.setSelection(selectedLocation.getRoomId(),false);
    }

}
