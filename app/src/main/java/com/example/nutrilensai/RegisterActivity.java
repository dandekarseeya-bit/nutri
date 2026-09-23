package com.example.nutrilensai;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutrilensai.data.AppDatabase;
import com.example.nutrilensai.data.User;
import com.example.nutrilensai.data.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;

    private ExecutorService executorService;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        executorService = Executors.newSingleThreadExecutor();
        userDao = AppDatabase.getDatabase(this).userDao();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                executorService.execute(() -> {
                    User existingUser = userDao.getUserByUsername(username);
                    if (existingUser != null) {
                        runOnUiThread(() -> Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show());
                    } else {
                        User newUser = new User();
                        newUser.username = username;
                        newUser.password = password;
                        userDao.insert(newUser);
                        
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Registration successful. Please login.", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to login screen
                        });
                    }
                });
            }
        });
        
        tvLoginLink.setOnClickListener(v -> finish());
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}