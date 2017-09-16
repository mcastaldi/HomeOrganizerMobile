package castaldini.homeorganizationmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

/**
 * Class used to ask the user if they want to get a suggested location to store an item
 */
public class SuggestDialogFragment extends DialogFragment {
    private final String TAG = "AUC_SIMPLE";

    interface SuggestDialogListener {
        void onConfirmGetSuggestion(DialogFragment dialog);
        void onDenyGetSuggestion(DialogFragment dialog);
    }

    SuggestDialogListener mHost;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.get_suggested_storage_dialog_text)
                .setPositiveButton(R.string.get_suggested_storage_confirm_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Positive");
                        mHost.onConfirmGetSuggestion(SuggestDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.get_suggested_storage_cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Negative");
                        mHost.onDenyGetSuggestion(SuggestDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dlg){
        super.onCancel(dlg);
        mHost.onDenyGetSuggestion(SuggestDialogFragment.this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mHost = (SuggestDialogListener) context;
    }

}
