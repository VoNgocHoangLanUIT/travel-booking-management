package com.hotelmanagement.activities.customer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hotelmanagement.R;
import com.hotelmanagement.adapters.NotificationAdapter;
import com.hotelmanagement.database.entities.UserEntity;
import com.hotelmanagement.services.NotificationService;
import com.hotelmanagement.services.UserService;
import com.hotelmanagement.models.Notification;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageView btnBack = findViewById(R.id.btnBackNotif);
        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);
        TextView tvReadAll = findViewById(R.id.tvReadAll);

        btnBack.setOnClickListener(v -> finish());

        // Thiết lập RecyclerView
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        NotificationService notificationService = new NotificationService(this);
        UserService userService = new UserService(this);
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem thông báo", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        long userId = currentUser.id;

        List<Notification> notificationList = notificationService.getNotificationModelsByUser(userId);

        NotificationAdapter adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
    }
}
