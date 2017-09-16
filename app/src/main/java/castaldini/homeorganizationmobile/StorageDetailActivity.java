package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.Item;
import castaldini.homeorganizationmobile.model.Storage;

/**
 *  Class that controls the screen that shows the user information on a storage. Uses the item adapter class to show a list of the items in the class.
 */
public class StorageDetailActivity extends AppCompatActivity {

    public DataSource mDataSource;
    Storage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataSource = new DataSource(this);
        mDataSource.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_detail);

        storage = getIntent().getExtras().getParcelable(StorageAdapter.STORAGE_KEY);
        if (storage != null) {

            System.out.println(storage.toString());
            TextView tvStorageName = (TextView) findViewById(R.id.viewItemsStorageName);
            TextView tvStorageCost = (TextView) findViewById(R.id.viewItemsStorageCost);
            tvStorageName.setText(storage.getName());
            tvStorageCost.setText(storage.getCostAsString());

            List<Item> items = storage.getItems();
            ItemAdapter adapter = new ItemAdapter(this,items);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.itemsRecyclerView);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Error loading Storage", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteStorageButtonClicked(View view) {
        if(storage.getNumOfItems()>0){
            Toast.makeText(this, "Can't delete when there are items", Toast.LENGTH_SHORT).show();
        } else {
            mDataSource.deleteStorage(storage.getPrimaryKey());
            Intent intent = new Intent(StorageDetailActivity.this,HomeActivity.class);
            intent.putExtra("deleted","storage");
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDataSource.close();
    }
}
