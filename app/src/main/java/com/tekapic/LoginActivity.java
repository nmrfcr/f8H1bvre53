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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;


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


    public void login(View view) {

        if(isNetworkConnected() == false) {
            popUpAlertDialogConnectionError();
            return;
        }

        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();


        if(isValidEmailAddress(email) == false) {
            showAlertDialog("Error", "Enter a correct Email Address.");
            return;
        }

        if(TextUtils.isEmpty(email)) {
            showAlertDialog("Error", "Email cannot be empty.");
        }
        else if(TextUtils.isEmpty(password)) {
            showAlertDialog("Error", "Password cannot be empty.");
        }
        else {
            //sign in with firebase
            mDialog.setMessage("Please wait...");
            mDialog.show();
            mDialog.setCancelable(false);

            loginViaFirebase(email, password);
        }

    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void loginViaFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.dismiss();

                if(!task.isSuccessful()) {
                    //error registering users
                    Log.i("info", "error Login users");
                    Log.i("info", task.getException().getMessage());

                    showAlertDialog("Error", task.getException().getMessage());
                }
                else {
                    //Login success
                    //take the user to HomeActivity
                    finish();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.forgotPasswordMenu:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
        setContentView(R.layout.activity_login);

        mEmailEditText = findViewById(R.id.emailEditTextLogin);
        mPasswordEditText = findViewById(R.id.passwordEditTextLogin);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

    }
}
