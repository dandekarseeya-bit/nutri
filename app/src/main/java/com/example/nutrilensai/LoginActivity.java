package com.example.nutrilensai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.nutrilensai.data.AppDatabase;
import com.example.nutrilensai.data.User;
import com.example.nutrilensai.data.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    private ExecutorService executorService;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Check if already logged in
        SharedPreferences prefs = getSharedPreferences("NutriLensPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        executorService = Executors.newSingleThreadExecutor();
        userDao = AppDatabase.getDatabase(this).userDao();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                executorService.execute(() -> {
                    User user = userDao.login(username, password);
                    runOnUiThread(() -> {
                        if (user != null) {
                            prefs.edit().putBoolean("isLoggedIn", true).apply();
                            prefs.edit().putString("username", username).apply();
                            
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
        
        tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
