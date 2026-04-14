package com.hotelmanagement.activities.customer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvSignInLink = findViewById(R.id.tvSignInLink);

        btnSignUp.setOnClickListener(v -> {
            // Xử lý đăng ký và quay lại đăng nhập
            finish();
        });

        tvSignInLink.setOnClickListener(v -> finish());
    }
}
