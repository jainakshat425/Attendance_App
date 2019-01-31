package com.example.android.attendance;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username_edit_text)
    TextInputLayout usernameIn;
    String username="";

    @BindView(R.id.password_edit_text)
    TextInputLayout passIn;
    String pass="";

    @OnClick(R.id.login_button)
    void login() {
        username = Objects.requireNonNull(usernameIn.getEditText()).getText().toString().trim();
        pass = Objects.requireNonNull(passIn.getEditText()).getText().toString().trim();

        if (ExtraUtils.isNetworkAvailable(this)) {
            if (validateInputs()) {
                VolleyTask.login(this, username, pass, jObj -> {
                    try {

                        int facId = jObj.getInt("fac_id");
                        String facName = jObj.getString(SharedPrefManager.FAC_NAME);
                        String facUsername = jObj.getString(SharedPrefManager.FAC_EMAIL);
                        String facDept = jObj.getString(SharedPrefManager.FAC_DEPT);
                        int fCollId = jObj.getInt(SharedPrefManager.FAC_COLL_ID);
                        String mobNo = jObj.getString(SharedPrefManager.FAC_MOB_NO);

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
                });
            }
        }
        else
            Toast.makeText(this, R.string.network_not_available, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.need_help_button)
    void needHelp() {
        Intent needHelpIntent = new Intent(Intent.ACTION_SEND);
        needHelpIntent.setType("text/html");
        needHelpIntent.putExtra(Intent.EXTRA_SUBJECT, "Need Help");
        needHelpIntent.putExtra(Intent.EXTRA_TEXT, "Describe the problem");
        startActivity(Intent.createChooser(needHelpIntent, "Send Email..."));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    private boolean validateInputs() {
        if (!isValidEmail())
            return false;
        else return isValidPass();
    }

    private boolean isValidPass() {
        if (TextUtils.isEmpty(pass) || pass.length() < 8) {
            passIn.setError("Password must contain minimum 8 characters.");
            return false;
        } else {
            passIn.setError(null);
            return true;
        }
    }

    private boolean isValidEmail() {
        if (TextUtils.isEmpty(username) || !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            usernameIn.setError("Enter valid Email Address.");
            return false;
        } else {
            usernameIn.setError(null);
            return true;
        }
    }
}


