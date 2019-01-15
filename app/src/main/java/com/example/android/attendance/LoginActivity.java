package com.example.android.attendance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;
import com.example.android.attendance.volley.VolleyCallback;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button needHelpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        needHelpButton = findViewById(R.id.need_help_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        needHelpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent needHelpIntent = new Intent(Intent.ACTION_SEND);
                needHelpIntent.setType("text/html");
                needHelpIntent.putExtra(Intent.EXTRA_SUBJECT, "Need Help");
                needHelpIntent.putExtra(Intent.EXTRA_TEXT, "Describe the problem");
                startActivity(Intent.createChooser(needHelpIntent, "Send Email..."));
            }
        });
    }

    private void login() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        VolleyTask.login(this, username, password, new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject jObj) {
                try {
                    Toast.makeText(LoginActivity.this, jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();

                    int facId = jObj.getInt("fac_id");
                    String facName = jObj.getString(FacultyEntry.F_NAME_COL);
                    String facUsername = jObj.getString(FacultyEntry.F_USERNAME_COL);
                    String facDept = jObj.getString("dept_name");
                    int fCollId = jObj.getInt(FacultyEntry.F_COLLEGE_ID);
                    String mobNo = jObj.getString(FacultyEntry.F_MOB_NO);

                    boolean saved = SharedPrefManager.getInstance(LoginActivity.this)
                            .saveLoginUserDetails(facId, facName, facUsername,
                                    facDept, fCollId, mobNo);
                    if (saved) {
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


