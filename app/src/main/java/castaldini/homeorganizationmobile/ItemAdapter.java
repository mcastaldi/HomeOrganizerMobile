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

import castaldini.homeorganizationmobile.model.Item;

/**
 *  Class used to fill the storage detail activity with the list of items
 */

class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{

    static final String ITEM_KEY = "item_key";
    private List<Item> mItems;
    private Context mContext;

    ItemAdapter(Context context, List<Item> items){
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.list_item,parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        final Item item = mItems.get(position);

        holder.tvItemBarcode.setText(item.getCostAsString());
        holder.tvItemName.setText(item.getName());
        Resources res = mContext.getResources();
        String itemCount = res.getString(R.string.item_count,item.getCount());
        holder.tvItemCount.setText(itemCount);

        //listener that allows the user to go to the item detail screen
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "You selected " + item.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ItemDetailActivity.class);
                intent.putExtra(ITEM_KEY, item);
                mContext.startActivity(intent);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "You long clicked " + item.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItemBarcode;
        TextView tvItemName;
        TextView tvItemCount;
        View mView;
        ViewHolder(View itemView) {
            super(itemView);

            tvItemBarcode = (TextView) itemView.findViewById(R.id.viewItemListBarcode);
            tvItemName = (TextView) itemView.findViewById(R.id.viewItemListName);
            tvItemCount = (TextView) itemView.findViewById(R.id.viewItemListCount);
            mView = itemView;
        }
    }
}
