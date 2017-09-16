package castaldini.homeorganizationmobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.model.ItemLocation;

/**
 * SuggestedResultsDialogFragment used to show the suggested location is a popup dialog
 */

public class SuggestedResultsDialogFragment extends DialogFragment implements ItemLocationAdapter.ItemLocationAdapterListener {
    private SuggestedResultsListener mListener;

    public static SuggestedResultsDialogFragment newInstance(List<ItemLocation> itemLocations){

        SuggestedResultsDialogFragment fragment = new SuggestedResultsDialogFragment();
        if(itemLocations!=null) {
            Bundle args = new Bundle();
            ArrayList<String> locationsAsStrings = new ArrayList<>();
            for (ItemLocation il : itemLocations) {
                locationsAsStrings.add(il.toString());
            }
            args.putStringArrayList("LOCATION_LIST", locationsAsStrings);
            fragment.setArguments(args);
            Log.i("AUC_LOCATION", "completeNewInstance");
        }

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            //noinspection ConstantConditions
            final String[] locations = getArguments().getStringArrayList("LOCATION_LIST").toArray(new String[0]);

            builder.setTitle("Suggested Locations");

            builder.setItems(locations, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Log.i(TAG, String.format("Location chosen: %s", locations[i]));
                    mListener.onSelectSuggestedLocation(i);
                }
            });
        } catch (NullPointerException ignored){

        }

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.suggested_locations_layout,container,false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mListener = (SuggestedResultsListener) context;
    }

    @Override
    public void onItemLocationSelected(int pos) {
        mListener.onSelectSuggestedLocation(pos);
    }

    interface SuggestedResultsListener {
        void onSelectSuggestedLocation(int pos);
        void onCancelSuggestedLocation();
    }

    @Override
    public void onCancel(DialogInterface dlg){
        super.onCancel(dlg);
        mListener.onCancelSuggestedLocation();
    }

}
