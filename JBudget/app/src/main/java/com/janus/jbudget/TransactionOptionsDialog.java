package com.janus.jbudget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class TransactionOptionsDialog extends DialogFragment {

    private static final String ARG_TRANS_DIALOG_INDEX = "trans_dialog_selected_index";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int transIdx = getArguments().getInt(ARG_TRANS_DIALOG_INDEX);

        String options[] = {"Edit", "Delete"};

        builder.setTitle("Transaction")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0:
                                editTransaction(transIdx);
                                break;
                            case 1:
                                deleteTransaction(transIdx);
                                break;
                            default:
                                break;
                        }
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }


    public void deleteTransaction(int transIdx) {

        Log.d("Main", "Delete transaction");
       TransactionFragment.deleteTransction(transIdx);

    }

    public void editTransaction(int transIdx) {
        Log.d("Main", "Edit transaction");

        Intent intent = new Intent(getActivity(), AddTransactionActivity.class);
        Bundle b = new Bundle();
        b.putInt(ARG_TRANS_DIALOG_INDEX, transIdx);
        intent.putExtras(b);
        startActivity(intent);
    }
}
