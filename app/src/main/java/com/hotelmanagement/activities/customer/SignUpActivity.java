package com.hotelmanagement.activities.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hotelmanagement.R;
import com.hotelmanagement.services.UserService;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText etFullName = findViewById(R.id.etFullNameSignUp);
        EditText etPhone = findViewById(R.id.etPhoneSignUp);
        EditText etEmail = findViewById(R.id.etEmailSignUp);
        EditText etPassword = findViewById(R.id.etPasswordSignUp);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvSignInLink = findViewById(R.id.tvSignInLink);

        UserService userService = new UserService(this);

        btnSignUp.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String confirm = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

            if (TextUtils.isEmpty(fullName)) {
                Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userService.signUp(fullName, email, password, phone, false) == null) {
                Toast.makeText(this, "Email đã tồn tại hoặc đăng ký thất bại", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        tvSignInLink.setOnClickListener(v -> finish());
    }
}
