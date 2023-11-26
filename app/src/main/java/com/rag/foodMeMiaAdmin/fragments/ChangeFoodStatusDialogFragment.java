package com.rag.foodMeMiaAdmin.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.rag.foodMeMiaAdmin.R;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.UpdateData;

public class ChangeFoodStatusDialogFragment extends DialogFragment {
    boolean availableStatus = false;
    String uniqueId;

    public ChangeFoodStatusDialogFragment(String uniqueId, boolean availableStatus) {
        this.availableStatus = availableStatus;
        this.uniqueId = uniqueId;
    }

    //    @SuppressLint("ResourceAsColor")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.availability_status_change_dialog, null);
        TextView viewText = (TextView) view.findViewById(R.id.statusTextDialogBox);
        Button button = (Button) view.findViewById(R.id.switchAvailableBtn);
        System.out.println("id is " + uniqueId);
        if (!availableStatus) {
            viewText.setText("Unavailable");
            viewText.setTextColor(Color.parseColor("#C70039"));
            button.setText("Make Available");

        } else {
            viewText.setText("available");
            viewText.setTextColor(Color.parseColor("#0BDA51"));
            button.setText("Make Unavailable");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                UpdateData.updateFoodStatus(uniqueId, !availableStatus)
                        .subscribe(
                                document -> {
                                    dismiss();

                                }
                                ,
                                throwable -> {

                                }
                        );
            }
        });
        builder.setView(view);


        return builder.create();
    }
}
