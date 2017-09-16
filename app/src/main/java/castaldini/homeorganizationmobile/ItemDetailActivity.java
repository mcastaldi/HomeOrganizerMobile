package castaldini.homeorganizationmobile;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import castaldini.homeorganizationmobile.database.DataSource;
import castaldini.homeorganizationmobile.model.Item;

/**
 *  Class that controls the item detail screen. Shows the user information about the items and allows them to add or remove one, or delete the item.
 */
public class ItemDetailActivity extends AppCompatActivity {
    public DataSource mDataSource;
    TextView tvItemCount;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataSource = new DataSource(this);
        mDataSource.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        item = getIntent().getExtras().getParcelable(ItemAdapter.ITEM_KEY);
        if(item != null) {
            TextView tvRoomName = (TextView) findViewById(R.id.itemDetailRoomName);
            TextView tvStorageName = (TextView) findViewById(R.id.itemDetailStorageName);
            TextView tvItemName = (TextView) findViewById(R.id.itemDetailItemName);
            TextView tvItemBarcode = (TextView) findViewById(R.id.itemDetailItemBarcode);
            TextView tvItemCost = (TextView) findViewById(R.id.itemDetailItemCost);
            tvItemCount = (TextView) findViewById(R.id.itemDetailItemCount);

            Resources res = getResources();
            tvRoomName.setText(res.getString(R.string.item_detail_room,mDataSource.getRoomName(item.getRoomId())));
            tvStorageName.setText(res.getString(R.string.item_detail_storage, mDataSource.getStorageName(item.getStorageId())));
            tvItemName.setText(item.getName());
            tvItemBarcode.setText(res.getString(R.string.item_detail_barcode,item.getBarcode()));
            tvItemCost.setText(res.getString(R.string.item_detail_cost,item.getCostAsString()));
            tvItemCount.setText(String.valueOf(item.getCount()));
        } else {
            Toast.makeText(this, "Error loading item", Toast.LENGTH_SHORT).show();
        }
    }

    public void incrementItemCountButtonClicked(View view) {
        tvItemCount.setText(String.valueOf(item.increment(mDataSource)));
    }

    public void decrementItemCountButtonClicked(View view) {
        tvItemCount.setText(String.valueOf(item.decrement(mDataSource)));
    }

    public void deleteItemButtonClicked(View view) {
        int key = item.getPrimaryKey();
        mDataSource.deleteItem(key);
        Intent intent = new Intent(ItemDetailActivity.this,HomeActivity.class);
        intent.putExtra("deleted","item");
        startActivity(intent);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mDataSource.close();
    }
}
