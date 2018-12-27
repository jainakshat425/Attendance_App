package com.example.android.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.utilities.ExtraUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.FAC_LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                Toast.makeText(LoginActivity.this, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                                int facId = jObj.getInt(FacultyEntry._ID);
                                String facName = jObj.getString(FacultyEntry.F_NAME_COL);
                                String facUsername = jObj.getString(FacultyEntry.F_USERNAME_COL);
                                String facDept = jObj.getString(FacultyEntry.F_DEPARTMENT_COL);

                                boolean saved = SharedPrefManager.getInstance(LoginActivity.this)
                                        .saveLoginUserDetails(facId, facName, facUsername, facDept);
                                if (saved) {
                                    finish();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(FacultyEntry.F_USERNAME_COL, username);
                params.put(FacultyEntry.F_PASSWORD_COL, password);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
    }
}


