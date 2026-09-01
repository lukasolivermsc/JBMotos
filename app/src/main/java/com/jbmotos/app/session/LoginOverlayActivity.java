package com.jbmotos.app.session;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.jbmotos.app.R;

public class LoginOverlayActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_overlay);

        SessionManager sessionManager = SessionManager.getInstance(getBaseContext().getApplicationContext());

        EditText usernameInput = findViewById(R.id.editUsername);

        Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            if (!username.isEmpty()) {
                sessionManager.login(new User.UserData(username));
                setResult(RESULT_OK);
                finish();
            }
        });

        ImageButton closeButton = findViewById(R.id.btnCloseLogin);
        closeButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
