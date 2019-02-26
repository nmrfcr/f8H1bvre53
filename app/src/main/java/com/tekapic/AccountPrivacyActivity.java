package com.tekapic;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AccountPrivacyActivity extends AppCompatActivity {

    private Switch aSwitch;
    private boolean isSwitchCheckSetted;

    public void popUpPrivacyAlertDialog(final String privacy, int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountPrivacyActivity.this);
        builder.setTitle("Change to " + privacy + " Account?");

        builder.setMessage(message);

        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSwitchCheckSetted = true;
                if(privacy.equals("Private")) {
                    aSwitch.setChecked(false);
                }
                else {
                    aSwitch.setChecked(true);
                }
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_privacy);

        isSwitchCheckSetted = false;

        aSwitch = findViewById(R.id.privateAccountSwitch);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isSwitchCheckSetted) {
                    isSwitchCheckSetted = false;
                    return;
                }

                if(isChecked) {

                    if(isNetworkConnected() == false) {
                        aSwitch.setChecked(false);
                        popUpAlertDialogConnectionError();
                        return;
                    }

                    popUpPrivacyAlertDialog("Private", R.string.privacy_private_description);
                }
                else {

                    if(isNetworkConnected() == false) {
                        aSwitch.setChecked(true);
                        popUpAlertDialogConnectionError();
                        return;
                    }

                    popUpPrivacyAlertDialog("Public", R.string.privacy_public_description);
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }
}
