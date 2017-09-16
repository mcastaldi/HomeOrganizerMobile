package castaldini.homeorganizationmobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 *  Class used to ask the user whether or not they want to use the barcode scanner
 */

public class ScannerPromptDialogFragment extends DialogFragment {

    interface ScannerPromptListener{
        void onConfirmScanner(DialogFragment dialog);
        void onCancelScanner(DialogFragment dialog);
    }

    ScannerPromptListener mHost;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("This will open your camera, asking permission if needed, and allow you to scan a barcode to have it entered automatically. " +
                "After the camera focuses, and a box appears around the barcode, tap it to have it entered. This message can be disabled in settings.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onConfirmScanner(ScannerPromptDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHost.onCancelScanner(ScannerPromptDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dlg){
        super.onCancel(dlg);
        mHost.onCancelScanner(ScannerPromptDialogFragment.this);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mHost = (ScannerPromptListener) context;
    }
}
