package com.tekapic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private boolean isPasswordSent = false;


    private  boolean isValidEmailAddress(String email) {

        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);

        if(matcher.find() == false) {
            //Wrong email format
            return false;
        }

        return true;
    }

    public void resetPassword(View view) {


        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        String email = mEmailEditText.getText().toString();

        if(TextUtils.isEmpty(email)) {
            showAlertDialog("Error", "Email cannot be empty.");
        }
        else if(isValidEmailAddress(email) == false) { // check email format
            showAlertDialog("Error", "Enter a correct Email Address.");
        }
        else {
            mDialog.setMessage("Please wait...");
            mDialog.show();
            mDialog.setCancelable(false);
            //send email to server for password reseting
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful()) {
                        isPasswordSent = true;
                        showAlertDialog("", "Please check your email account.");
                    }
                    else {
                        showAlertDialog("Error", task.getException().getMessage());
                    }
                }
            });
        }

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(isPasswordSent) {
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                }
            }
        });
        builder.create().show();
    }

    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }

    private void popUpAlertDialogConnectionError() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Error");
        builder1.setMessage("There might be problems with the server or network connection.");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "TRY AGAIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        if(isNetworkConnected() == false) {
//                            popUpAlertDialogConnectionError();
//                        }

                    }
                });

        AlertDialog alertDialog = builder1.create();
        alertDialog.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        mEmailEditText = findViewById(R.id.emailEditTextForgotPassword);
        mDialog = new ProgressDialog(this);

    }
}
